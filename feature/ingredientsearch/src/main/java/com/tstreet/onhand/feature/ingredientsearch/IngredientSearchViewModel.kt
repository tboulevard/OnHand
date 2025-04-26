package com.tstreet.onhand.feature.ingredientsearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.usecase.ingredientsearch.IngredientSearchUseCase
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Provider

abstract class IngredientSearchViewModel(
    private val searchIngredients: Provider<IngredientSearchUseCase>,
    private val mapper: SearchUiStateMapper,
    ioDispatcher: CoroutineDispatcher
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

    fun onSearchTextChanged(text: String) {
        _searchTextFlow.tryEmit(text)
    }

    protected val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    abstract fun onItemClick(uiSearchIngredient: UiSearchIngredient)
}
