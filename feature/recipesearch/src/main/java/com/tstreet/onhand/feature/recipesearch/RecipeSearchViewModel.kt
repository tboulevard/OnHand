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
import com.tstreet.onhand.core.model.ui.IngredientAvailability
import com.tstreet.onhand.core.model.ui.RecipeSaveState
import com.tstreet.onhand.core.model.ui.RecipeSearchUiState
import com.tstreet.onhand.core.model.ui.RecipeWithSaveState
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val saveRecipeLock = Mutex()
    private val addToShoppingListLock = Mutex()

    fun onRecipeSaved(recipe: RecipeWithSaveState) {
        viewModelScope.launch {
            saveRecipeLock.withLock {
                val result = saveRecipe.get().invoke(recipe.preview)
                when (result.status) {
                    Status.SUCCESS -> {
                        recipe.saveState.value = RecipeSaveState.SAVED
                    }

                    Status.ERROR -> {
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
        viewModelScope.launch {
            saveRecipeLock.withLock {
                val result = unsaveRecipe.get().invoke(recipe.preview)
                when (result.status) {
                    Status.SUCCESS -> {
                        recipe.saveState.value = RecipeSaveState.NOT_SAVED
                    }

                    Status.ERROR -> {
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
            addToShoppingListLock.withLock {
                val result = addToShoppingList.get().invoke(
                    missingIngredients = recipe.preview.missedIngredients,
                    recipe = recipe.preview
                )
                when (result.status) {
                    Status.SUCCESS -> {
                        recipe.ingredientShoppingCartState.value =
                            IngredientAvailability.ALL_INGREDIENTS_AVAILABLE
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