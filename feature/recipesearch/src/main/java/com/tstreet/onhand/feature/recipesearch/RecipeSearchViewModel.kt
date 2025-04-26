package com.tstreet.onhand.feature.recipesearch

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.usecase.shoppinglist.AddToShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.DEFAULT_SORTING
import com.tstreet.onhand.core.domain.usecase.recipes.GetRecipesUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.SaveRecipeUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.SortBy
import com.tstreet.onhand.core.domain.usecase.recipes.UnsaveRecipeUseCase
import com.tstreet.onhand.core.model.ui.RecipeSearchUiState
import com.tstreet.onhand.core.model.ui.RecipeWithSaveState
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import com.tstreet.onhand.core.model.ui.RecipeSaveState.*
import com.tstreet.onhand.core.model.ui.toRecipeWithSaveStateItemList
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
        Log.d("[OnHand]", "${this.javaClass.simpleName} created")
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
                        displayed(
                            title = "Error",
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

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _errorDialogState.value
        )

    private val _infoDialogState = MutableStateFlow(dismissed())
    val infoDialogState = _infoDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _infoDialogState.value
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
            saveRecipe.get().invoke(item.recipePreview).collect {
                when (it) {
                    // When save is successful, update UI state
                    true -> {
                        _recipes[index] = item.copy(
                            recipeSaveState = SAVED
                        )
                    }
                    else -> {
                        // Retain the previous save state on error
                        _recipes[index] = item.copy(
                            recipeSaveState = saveState
                        )
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = "Recipe save unsuccessful, there was an error. " +
                                        "Please try again."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onRecipeUnsaved(index: Int) {
        viewModelScope.launch {
            val item = _recipes[index]
            // Just unsave the recipe - no loading indicator
            unsaveRecipe.get().invoke(item.recipePreview).collect {
                when (it) {
                    // When the unsave is successful, update UI state
                    true -> {
                        _recipes[index] = item.copy(
                            recipeSaveState = NOT_SAVED
                        )
                    }
                    else -> {
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = "Recipe unsave unsuccessful, there was an error. " +
                                        "Please try again."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAddToShoppingList(index: Int) {
        viewModelScope.launch {
            val item = _recipes[index]
            addToShoppingList.get().invoke(
                // TODO: .map for getting from RecipeIngredient -> Ingredient
                ingredients = item.recipePreview.missedIngredients.map { it.ingredient },
                recipePreview = item.recipePreview
            ).collect {
                when (it.status) {
                    SUCCESS -> {
                        // TODO: implement logic to transmit state back to UI
                    }
                    ERROR -> {
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = "Unable to add ingredients to shopping list. Please " +
                                        "try again."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSortOrderChanged(sortingOrder: SortBy) {
        _sortOrder.update { sortingOrder }
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }

    fun showInfoDialog() {
        _infoDialogState.update {
            displayed(
                title = "Search Recipes",
                message = "Recipes shown here are based on ingredients from your pantry. By " +
                        "default we'll only show recipes where you're missing at most 3 " +
                        "ingredients.",
            )
        }
    }

    fun dismissInfoDialog() {
        _infoDialogState.update { dismissed() }
    }
}
