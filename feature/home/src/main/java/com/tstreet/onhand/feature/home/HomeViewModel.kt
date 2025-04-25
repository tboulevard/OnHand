package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.Status.*
import com.tstreet.onhand.core.domain.usecase.ingredientsearch.IngredientSearchUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.GetPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.SearchUiState
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
@FeatureScope
class HomeViewModel @Inject constructor(
    private val searchIngredients: Provider<IngredientSearchUseCase>,
    private val addToPantry: Provider<AddToPantryUseCase>,
    private val removeFromPantry: Provider<RemoveFromPantryUseCase>,
    getPantry: Provider<GetPantryUseCase>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
    private val mapper: HomeUiStateMapper
) : ViewModel() {

    init {
        Log.d("[OnHand]", "${this.javaClass.simpleName} created")
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

    private val _searchUiState: Flow<SearchUiState> = _searchTextFlow
        .debounce(250L)
        .flatMapLatest { searchQuery ->
            searchIngredients.get().getPantryMapped(searchQuery)
        }.map { searchResult ->
            mapper.mapSearchResultToSearchUi(searchResult)
        }.flowOn(ioDispatcher)

    val searchUiState: StateFlow<SearchUiState> =
        _searchUiState
            .stateIn(
                // Note: Child jobs launched in this scope are automatically cancelled when
                //  onCleared() is called for this ViewModel.
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SearchUiState.Empty
            )

    private val _isSearchBarFocused = MutableStateFlow(false)
    val isSearchBarFocused = _isSearchBarFocused.asStateFlow()

    val pantryUiState: StateFlow<PantryUiState> =
        _isSearchBarFocused.flatMapLatest { searchBarFocused ->
            // If we're not focused on search bar, re-evaluate
            if (!searchBarFocused) {
                getPantry.get().invoke()
                    .map {
                        mapper.mapPantryListResultToPantryUi(it)
                    }
            } else {
                flowOf(PantryUiState.None)
            }
        }.filter {
            it !is PantryUiState.None
        }
            .flowOn(ioDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PantryUiState.Loading
            )

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

    fun onToggleIngredient(pantryIngredient: UiPantryIngredient) {
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

    fun onSearchBarFocusChanged(isFocused: Boolean) {
        _isSearchBarFocused.update { isFocused }
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }

    override fun onCleared() {
        // TODO: Not called, lifecycle not properly managed
        Log.d("[OnHand]", "$TAG cleared")
        super.onCleared()
    }
}

private val TAG = HomeViewModel::class.simpleName
