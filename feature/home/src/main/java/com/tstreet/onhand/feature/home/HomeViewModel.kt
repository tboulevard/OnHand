package com.tstreet.onhand.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.ingredients.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.PantryIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

// TODO: Each time we emit a value for either ingredient or pantry, the entire list recomposes.
//  Tried giving each element a key to avoid this but didn't work. Look into later. For now this
//  class is mostly just an example of how to use Flows from room really cleanly.
@OptIn(kotlinx.coroutines.FlowPreview::class)
class HomeViewModel @Inject constructor(
    private val getIngredients: Provider<GetIngredientsUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    getPantry: Provider<GetPantryUseCase>,
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private var searchQuery: String? = null

    // SharedFlow does not need to explicitly need to be collected, as it is a hot flow.
    // Addtionally, we can replay to all observers n times.
    private val _searchTextFlow = MutableSharedFlow<String?>(replay = 1)
    // However this is a regular Flow (cold), and needs to be collected. We collect it via
    // .collectAsState()
    val searchText: Flow<String> = _searchTextFlow.map { it.orEmpty() }

    private val _ingredients: Flow<List<PantryIngredient>> = _searchTextFlow
        .onEach {
            searchQuery = it
            _isPreSearchDebounce.update { true }
        }
        .debounce(250L)
        .onEach { _isPreSearchDebounce.update { false } }
        .flatMapLatest {
            _isSearching.update { true }
            // NOTE: This is retriggered when changing pantry state in search list too.
            getIngredients.get().invoke(searchQuery)
        }
        .onEach {
            _isSearching.update { false }
        }

    val ingredients: StateFlow<List<PantryIngredient>> =
        _ingredients
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val pantry: StateFlow<List<PantryIngredient>> =
        getPantry.get().invoke()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _isSearchBarFocused = MutableStateFlow(false)
    val isSearchBarFocused = _isSearchBarFocused.asStateFlow()

    private val _isPreSearchDebounce = MutableStateFlow(false)
    val isPreSearchDebounce = _isPreSearchDebounce.asStateFlow()

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    fun onSearchTextChanged(text: String) {
        _searchTextFlow.tryEmit(text)
    }

    fun onToggleFromSearch(index: Int) {
        viewModelScope.launch {
            val item = ingredients.value[index]
            when {
                item.inPantry -> {
                    when (removeFromPantry.get().invoke(item.ingredient).status) {
                        Status.SUCCESS -> { }
                        Status.ERROR -> {
                            _errorDialogState.update {
                                displayed(
                                    title = "Error",
                                    message = "Unable to remove item from pantry. Please try again."
                                )
                            }
                        }
                    }
                }
                else -> {
                    when (addToPantry.get().invoke(item.ingredient).status) {
                        Status.SUCCESS -> { }
                        Status.ERROR -> {
                            _errorDialogState.update {
                                displayed(
                                    title = "Error",
                                    message = "Unable to add item to pantry. Please try again."
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onToggleFromPantry(index: Int) {
        viewModelScope.launch {
            val item = pantry.value[index]
            // TODO: probably an unnecessary check, but put here to make sure we didn't somehow
            // get an ingredient in the pantry that isn't marked as in the pantry
            if (item.inPantry) {
                when (removeFromPantry.get().invoke(item.ingredient).status) {
                    Status.SUCCESS -> { }
                    Status.ERROR -> {
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = "Unable to remove item from pantry. Please try again."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSearchBarFocusChanged(isFocused: Boolean) {
        _isSearchBarFocused.update { isFocused }
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }
}
