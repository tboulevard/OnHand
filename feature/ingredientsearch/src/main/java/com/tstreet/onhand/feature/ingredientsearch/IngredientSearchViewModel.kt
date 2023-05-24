package com.tstreet.onhand.feature.ingredientsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.ingredients.GetIngredientsUseCase
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

    private var searchQuery: String? = null

    // SharedFlow does not need to explicitly need to be collected, as it is a hot flow.
    // Additionally, we can replay to all new observers n times (in this case just the
    // most recent value).
    private val _searchTextFlow = MutableSharedFlow<String?>(replay = 1)
    private val _selectableIngredientsMutableFlow =
        MutableSharedFlow<List<SelectableIngredient>>(replay = 1)
    private var _selectableIngredients = mutableListOf<SelectableIngredient>()

    // However this is a regular Flow (cold), and needs to be collected. We collect it via
    // .collectAsState()
    val displayedSearchText: Flow<String> = _searchTextFlow.map { it.orEmpty() }

    // Ingredients to show to user
    private val _ingredients: Flow<List<SelectableIngredient>> =
        _searchTextFlow.onEach {
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
            .map {
                val newList = it.map { pantryIngredient ->
                    val ingredient = pantryIngredient.ingredient
                    SelectableIngredient(
                        ingredient = ingredient,
                        isSelected = selectedIngredients.find { selectedIngredient -> selectedIngredient.ingredient.name == ingredient.name } != null
                    )
                }
                _selectableIngredients = newList.toMutableList()
                newList
            }


    val displayedIngredients: Flow<List<SelectableIngredient>> =
        flowOf(_ingredients, _selectableIngredientsMutableFlow)
            // Allows us to collect only the most recently emitted value from the original
            // flows
            .flattenMerge()
            .onEach {
                println("[OnHand] post flatten concat")
            }

    private val selectedIngredients = mutableListOf<SelectableIngredient>()

    // Search-related fields

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _isSearchBarFocused = MutableStateFlow(false)
    val isSearchBarFocused = _isSearchBarFocused.asStateFlow()

    private val _isPreSearchDebounce = MutableStateFlow(false)
    val isPreSearchDebounce = _isPreSearchDebounce.asStateFlow()

    // Error

    private val _errorDialogState = MutableStateFlow(AlertDialogState.dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    // Functions

    fun onSearchTextChanged(text: String) {
        _searchTextFlow.tryEmit(text)
    }

    fun onSearchBarFocusChanged(isFocused: Boolean) {
        _isSearchBarFocused.update { isFocused }
    }

    fun onToggleIngredient(index: Int) {
        viewModelScope.launch {
            val item = _selectableIngredients[index]
            val isSelected = item.isSelected
            _selectableIngredients[index] = item.copy(isSelected = !isSelected)
            _selectableIngredientsMutableFlow.tryEmit(_selectableIngredients)
        }
    }

    fun getSelectedIngredients(): List<SelectableIngredient> {
        return selectedIngredients
    }
}
