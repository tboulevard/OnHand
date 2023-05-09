package com.tstreet.onhand.feature.recipesearch

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.*
import com.tstreet.onhand.core.domain.recipes.*
import com.tstreet.onhand.core.domain.shoppinglist.AddToShoppingListUseCase
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.ui.ErrorDialogState
import com.tstreet.onhand.core.ui.RecipeSaveState.*
import com.tstreet.onhand.core.ui.RecipeWithSaveState
import com.tstreet.onhand.core.ui.RecipeSearchUiState
import com.tstreet.onhand.core.ui.toRecipeWithSaveStateItemList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class RecipeSearchViewModel @Inject constructor(
    getRecipes: Provider<GetRecipesUseCase>,
    private val saveRecipe: Provider<SaveRecipeUseCase>,
    private val unsaveRecipe: Provider<UnsaveRecipeUseCase>,
    private val addToShoppingList: Provider<AddToShoppingListUseCase>
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private val _sortOrder = MutableStateFlow(DEFAULT_SORTING)
    private var _recipes = mutableStateListOf<RecipeWithSaveState>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sortOrder: StateFlow<SortBy> = _sortOrder
        .flatMapLatest {
            getRecipes.get().invoke(it)
        }
        .combine(_sortOrder) { recipes, sortBy ->
            when (recipes.status) {
                SUCCESS -> {
                    // TODO: Log analytics if data is null somehow. We fallback to emitting an
                    //  empty list.
                    _recipes = recipes.data.toRecipeWithSaveStateItemList()
                    // We pass the snapshot state list by reference to allow mutations within the ViewModel
                    _uiState.update { RecipeSearchUiState.Success(_recipes) }
                }
                ERROR -> {
                    // TODO: Log analytics if data is null somehow. We fallback to emitting an
                    //  empty list.
                    _recipes = recipes.data.toRecipeWithSaveStateItemList()
                    _uiState.update {
                        RecipeSearchUiState.Error(_recipes)
                    }
                    _errorDialogState.update {
                        ErrorDialogState(
                            shouldDisplay = true,
                            message = recipes.message.toString()
                        )
                    }
                }
            }
            sortBy
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _sortOrder.value
        )

    private val _uiState = MutableStateFlow<RecipeSearchUiState>(RecipeSearchUiState.Loading)
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _uiState.value
        )

    private val _errorDialogState = MutableStateFlow(
        ErrorDialogState(shouldDisplay = false)
    )
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _errorDialogState.value
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

    fun dismissErrorDialog() {
        _errorDialogState.update { ErrorDialogState(shouldDisplay = false) }
    }

    fun onAddToShoppingList(index: Int) {
        viewModelScope.launch {
            val item = _recipes[index]

            addToShoppingList.get().invoke(
                ingredients = emptyList(),
                recipe = item.recipe
            )
        }
        val item = _recipes[index]
        println("[OnHand] Adding missing ingredients for $item")
        // TODO: implement
    }
}
