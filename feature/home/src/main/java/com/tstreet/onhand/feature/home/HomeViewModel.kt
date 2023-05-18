package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
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

@OptIn(kotlinx.coroutines.FlowPreview::class)
class HomeViewModel @Inject constructor(
    private val getIngredients: Provider<GetIngredientsUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    getPantry: Provider<GetPantryUseCase>,
) : ViewModel() {

    // TODO: refactor to work with keys for each item in LazyColumn
    var ingredients = mutableStateListOf<PantryIngredient>()
        private set

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
        .onEach { _isPreSearchDebounce.update { true } }
        .debounce(250L)
        .onEach {
            _isPreSearchDebounce.update { false }
            // Only search and update listed ingredients if we have a valid search query
            if (it.isNotBlank()) {
                _isSearching.update { true }
                ingredients = getIngredients.get().invoke(it).toMutableStateList()
            } else if (ingredients.isNotEmpty()) {
                // Clear the list if search query is blank and we already have listed ingredients
                ingredients.clear()
            }
        }
        // TODO: is there a better operator than combine to appropriately set the backing field?
        .combine(_searchText) { _, _ ->
            _searchText.value
        }
        .onEach {
            _isSearching.update { false }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _searchText.value
        )

    val pantry: StateFlow<List<PantryIngredient>> =
        // TODO: Each time we emit a value, the entire pantry list recomposes. Tried giving
        //  each element a key to avoid this but didn't work. Look into later. We would also
        //  maintain a separate list in this class that changes based on the diff of what this
        //  emits and it. But not worth the effort for now, no performance issues...
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
        _searchText.value = text
    }

    fun onToggleFromSearch(index: Int) {
        viewModelScope.launch {
            val item = ingredients[index]
            when {
                item.inPantry -> {
                    when (removeFromPantry.get().invoke(item.ingredient).status) {
                        Status.SUCCESS -> {
                            ingredients[index] = item.copy(inPantry = false)
                        }
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
                        Status.SUCCESS -> {
                            ingredients[index] = item.copy(inPantry = true)
                        }
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
