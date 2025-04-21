package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Status.*
import com.tstreet.onhand.core.domain.ingredientsearch.IngredientSearchUseCase
import com.tstreet.onhand.core.domain.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.ui.HomeViewUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import kotlin.jvm.javaClass

@OptIn(kotlinx.coroutines.FlowPreview::class)
class HomeViewModel @Inject constructor(
    private val searchIngredients: Provider<IngredientSearchUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    getPantry: Provider<GetPantryUseCase>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
    private val mapper: HomeUiStateMapper
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    // SharedFlow does not need to explicitly need to be collected, as it is a hot flow.
    // Addtionally, we can replay to all observers n times.
    private val _searchTextFlow = MutableSharedFlow<String>(replay = 1)

    // However this is a regular Flow (cold), and needs to be collected. We collect it via
    // .collectAsState()
    val displayedSearchText: StateFlow<String> = _searchTextFlow
        .stateIn(
            // Note: Child jobs launched in this scope are automatically cancelled when
            //  onCleared() is called for this ViewModel.
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val _uiState: Flow<HomeViewUiState> = _searchTextFlow
        .debounce(250L)
        .flatMapLatest { searchQuery ->
            searchIngredients.get().getPantryMapped(searchQuery)
        }.map { searchResult ->
            mapper.mapSearchResultToHomeUi(searchResult)
        }.flowOn(ioDispatcher)

    val uiState: StateFlow<HomeViewUiState> =
        _uiState
            .stateIn(
                // Note: Child jobs launched in this scope are automatically cancelled when
                //  onCleared() is called for this ViewModel.
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeViewUiState.Empty
            )

    val pantry: StateFlow<List<Ingredient>> =
        getPantry.get().invoke()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _isSearchBarFocused = MutableStateFlow(false)
    val isSearchBarFocused = _isSearchBarFocused.asStateFlow()

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    fun onSearchTextChanged(text: String) {
        println("[OnHand] onSearchTextChanged: $text")
        _searchTextFlow.tryEmit(text)
    }

    fun onToggleFromSearch(pantryIngredient: UiPantryIngredient) {
        viewModelScope.launch {
            val inPantry = pantryIngredient.inPantry.value
            when {
                pantryIngredient.inPantry.value -> {
                    when (removeFromPantry.get().invoke(pantryIngredient.ingredient).status) {
                        SUCCESS -> {
                            pantryIngredient.inPantry.value = !inPantry
                        }

                        ERROR -> {
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
                    when (addToPantry.get().invoke(pantryIngredient.ingredient).status) {
                        SUCCESS -> {
                            pantryIngredient.inPantry.value = !inPantry
                        }

                        ERROR -> {
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
//            if (item.inPantry) {
//                when (removeFromPantry.get().invoke(item).status) {
//                    SUCCESS -> {}
//                    ERROR -> {
//                        _errorDialogState.update {
//                            displayed(
//                                title = "Error",
//                                message = "Unable to remove item from pantry. Please try again."
//                            )
//                        }
//                    }
//                }
//            }
        }
    }

    fun onSearchBarFocusChanged(isFocused: Boolean) {
        _isSearchBarFocused.update { isFocused }
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }

    override fun onCleared() {
        // TODO: Not called, lifecycle not properly managed
        Log.d("OnHand", "$TAG cleared")
        super.onCleared()
    }
}

private val TAG = HomeViewModel::class.simpleName
