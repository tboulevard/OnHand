package com.tstreet.onhand.feature.recipesearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status
import com.tstreet.onhand.core.domain.usecase.shoppinglist.AddToShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.DEFAULT_SORTING
import com.tstreet.onhand.core.domain.usecase.recipes.GetRecipesUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.SaveRecipeUseCase
import com.tstreet.onhand.core.domain.usecase.recipes.SortBy
import com.tstreet.onhand.core.domain.usecase.recipes.UnsaveRecipeUseCase
import com.tstreet.onhand.core.model.ui.RecipeSaveState
import com.tstreet.onhand.core.model.ui.RecipeSearchUiState
import com.tstreet.onhand.core.model.ui.RecipeWithSaveState
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeSearchViewModel @Inject constructor(
    getRecipes: Provider<GetRecipesUseCase>,
    private val saveRecipe: Provider<SaveRecipeUseCase>,
    private val unsaveRecipe: Provider<UnsaveRecipeUseCase>,
    private val addToShoppingList: Provider<AddToShoppingListUseCase>,
    private val mapper: SearchRecipeUiStateMapper,
    @Named("IO") ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    init {
        Log.d("[OnHand]", "${this.javaClass.simpleName} created")
    }

    private val _sortOrder = MutableStateFlow(DEFAULT_SORTING)
    val sortOrder = _sortOrder.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DEFAULT_SORTING
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<RecipeSearchUiState> = _sortOrder
        .flatMapLatest {
            getRecipes.get().invoke(it)
        }.map { getRecipesResult ->
            mapper.mapGetRecipesResultToUi(getRecipesResult)
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RecipeSearchUiState.Loading
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

    fun onRecipeSaved(recipe: RecipeWithSaveState) {
        // TODO: wrap all this in a lock to prevent concurrent execution. in general make
        //  mutable states visible to only one thread
        viewModelScope.launch {
            // Save the recipe
            saveRecipe.get().invoke(recipe.preview)
                .onStart {
                    // TODO: make the entire item unclickable during this time
                    recipe.saveState.value = RecipeSaveState.LOADING
                }
                .collect {
                    when (it) {
                        // When save is successful, update UI state
                        true -> {
                            recipe.saveState.value = RecipeSaveState.SAVED
                        }

                        else -> {
                            // Retain the previous save state on error
                            recipe.saveState.value = RecipeSaveState.NOT_SAVED
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

    fun onRecipeUnsaved(recipe: RecipeWithSaveState) {
// TODO: wrap all this in a lock to prevent concurrent execution. in general make
        //  mutable states visible to only one thread
        viewModelScope.launch {
            // Save the recipe
            unsaveRecipe.get().invoke(recipe.preview)
                .onStart {
                    recipe.saveState.value = RecipeSaveState.LOADING
                }
                .collect {
                    when (it) {
                        // When save is successful, update UI state
                        true -> {
                            recipe.saveState.value = RecipeSaveState.NOT_SAVED
                        }

                        else -> {
                            // Retain the previous save state on error
                            recipe.saveState.value = RecipeSaveState.SAVED
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

    fun onAddToShoppingList(recipe: RecipeWithSaveState) {
        viewModelScope.launch {
            addToShoppingList.get().invoke(
                // TODO: .map for getting from RecipeIngredient -> Ingredient
                missingIngredients = recipe.preview.missedIngredients,
                recipePreview = recipe.preview
            ).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        // TODO: Adding items to cart - propogate that missing items are in the cart?
                    }
                    Status.ERROR -> {
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