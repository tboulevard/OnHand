package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.ingredients.GetIngredientsUseCase
import com.tstreet.onhand.core.model.RecipeIngredient
import com.tstreet.onhand.core.ui.AlertDialogState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class IngredientSearchViewModel @Inject constructor(
    private val getIngredients: Provider<GetIngredientsUseCase>,
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    // Backing list for _selectableIngredientsFlow - mirrors what search query returns but allows
    // for mutations that are later sync
    private var _mutableIngredients = mutableListOf<SelectableIngredient>()

    // List of all selected ingredients only
    private val _selectedIngredients = mutableStateListOf<SelectableIngredient>()
    val displayedSelectedIngredients: List<SelectableIngredient> = _selectedIngredients

    // SharedFlow does not need to explicitly need to be collected, as it is a hot flow.
    // Additionally, we can replay to all new observers n times (in this case just the
    // most recent value).
    private val _searchTextFlow = MutableSharedFlow<String?>(replay = 1)

    // However this is a regular Flow (cold), and needs to be collected. We collect it via
    // .collectAsState()
    val displayedSearchText: Flow<String> = _searchTextFlow.map { it.orEmpty() }

    // Flow that intercepts changes in search query text
    private val _ingredients: Flow<List<SelectableIngredient>> =
        _searchTextFlow.onEach {
            _isPreSearchDebounce.update { true }
        }
            .debounce(250L)
            .onEach { _isPreSearchDebounce.update { false } }
            .flatMapLatest { searchQuery ->
                _isSearching.update { true }
                // NOTE: This is retriggered when changing pantry state in search list too.
                getIngredients.get().invoke(searchQuery)
            }
            .onEach {
                _isSearching.update { false }
            }
            .map {
                val newSearchResults = it.map { pantryIngredient ->
                    val ingredient = pantryIngredient.ingredient
                    SelectableIngredient(
                        ingredient = ingredient,
                        isSelected = _selectedIngredients.find { selectedIngredient -> selectedIngredient.ingredient.name == ingredient.name } != null
                    )
                }
                _mutableIngredients = newSearchResults.toMutableList()
                newSearchResults
            }

    private val _selectableIngredientsFlow =
        MutableSharedFlow<List<SelectableIngredient>>(replay = 1)

    val displayedIngredients: StateFlow<List<SelectableIngredient>> =
        flowOf(_ingredients, _selectableIngredientsFlow)
            // Allows us to collect only the most recently emitted value from the original
            // flows
            .flattenMerge()
            .stateIn(
                // Note: Child jobs launched in this scope are automatically cancelled when
                //  onCleared() is called for this ViewModel.
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

    private val _errorDialogState = MutableStateFlow(AlertDialogState.dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    fun onSearchTextChanged(text: String) {
        _searchTextFlow.tryEmit(text)
    }

    fun onSearchBarFocusChanged(isFocused: Boolean) {
        _isSearchBarFocused.update { isFocused }
    }

    fun onToggleIngredient(index: Int) {
        viewModelScope.launch {
            val item = _mutableIngredients[index]
            val isSelected = item.isSelected
            _mutableIngredients[index] = item.copy(isSelected = !isSelected)

            // Mirror the change by either adding or removing the element from the list of
            // selected ingredients
            if (isSelected) {
                _selectedIngredients.removeIf { it.ingredient.name == item.ingredient.name }
            } else {
                _selectedIngredients.add(_mutableIngredients[index])
            }

            _selectableIngredientsFlow.tryEmit(
                // To pass a new reference and trigger recomposition downstream
                _mutableIngredients.toList()
            )
        }
    }

    fun getSelectedIngredients(): List<RecipeIngredient> {
        return _selectedIngredients.map {
            RecipeIngredient(
                ingredient = it.ingredient,
                // TODO: revisit when we handle images
                image = "",
                // TODO: revisit when we handle quantities
                unit = "unit",
                amount = 0.0
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("[OnHand] IngredientSearchViewModel cleared")
    }
}
