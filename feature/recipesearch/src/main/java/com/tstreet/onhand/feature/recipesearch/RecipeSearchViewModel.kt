package com.tstreet.onhand.feature.recipesearch

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.*
import com.tstreet.onhand.core.ui.RecipeSaveState.*
import com.tstreet.onhand.core.ui.RecipeSearchItem
import com.tstreet.onhand.core.ui.RecipeSearchUiState
import com.tstreet.onhand.core.ui.toRecipeSearchItemList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class RecipeSearchViewModel @Inject constructor(
    getRecipes: Provider<GetRecipesUseCase>,
    private val saveRecipe: Provider<SaveRecipeUseCase>,
    private val unsaveRecipe: Provider<UnsaveRecipeUseCase>,
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private val _sortOrder = MutableStateFlow(DEFAULT_SORTING)
    private var _recipes = mutableStateListOf<RecipeSearchItem>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sortOrder: StateFlow<SortBy> = _sortOrder
        .flatMapLatest {
            getRecipes.get().invoke(it)
        }
        .combine(_sortOrder) { recipes, sortBy ->
            _recipes = recipes.toRecipeSearchItemList()
            // We pass the snapshot state list by reference to allow mutations within the ViewModel
            _uiState.update { RecipeSearchUiState.Success(_recipes) }
            sortBy
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _sortOrder.value
        )

    private val _uiState = MutableStateFlow<RecipeSearchUiState>(RecipeSearchUiState.Loading)
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _uiState.value
        )

    fun onRecipeSaved(index: Int) {
        // TODO: wrap all this in a lock to prevent concurrent execution. in general make
        //  mutable states visible to only one thread
        viewModelScope.launch {
            val item = _recipes[index]
            val saveState = item.recipeSaveState
            // Mark the recipe as saving
            _recipes[index] = item.copy(recipeSaveState = SAVING)
            // Save the recipe
            saveRecipe.get().invoke(item.recipe).collect {
                when (it) {
                    // When save is successful, update UI state
                    true -> {
                        _recipes[index] = item.copy(
                            recipeSaveState = SAVED
                        )
                    }
                    else -> {
                        // TODO: todo better error handling
                        println(
                            "[OnHand] Recipe save unsuccessful, there was an exception - " +
                                    "recipe not saved"
                        )
                        // Retain the previous save state on error
                        _recipes[index] = item.copy(
                            recipeSaveState = saveState
                        )
                    }
                }
            }
        }
    }

    fun onRecipeUnsaved(index: Int) {
        viewModelScope.launch {
            val item = _recipes[index]
            // Just unsave the recipe - no loading indicator
            unsaveRecipe.get().invoke(item.recipe).collect {
                when (it) {
                    // When the unsave is successful, update UI state
                    true -> {
                        _recipes[index] = item.copy(
                            recipeSaveState = NOT_SAVED
                        )
                    }
                    else -> {
                        // TODO: todo better error handling
                        println(
                            "[OnHand] Recipe unsave unsuccessful, there was an exception - " +
                                    "recipe not removed from DB"
                        )
                    }
                }
            }
        }
    }

    fun onSortOrderChanged(sortingOrder: SortBy) {
        _sortOrder.update { sortingOrder }
    }
}
