package com.tstreet.onhand.feature.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.shoppinglist.*
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import com.tstreet.onhand.core.ui.RecipeDetailUiState
import com.tstreet.onhand.core.ui.ShoppingListUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class ShoppingListViewModel @Inject constructor(
    getShoppingListUseCase: Provider<GetShoppingListUseCase>,
    getRecipesInShoppingListUseCase: Provider<GetRecipesInShoppingListUseCase>,
    private val removeIngredientUseCase: Provider<RemoveIngredientUseCase>,
    private val removeRecipeInShoppingListUseCase: Provider<RemoveRecipeInShoppingListUseCase>,
    private val checkIngredientUseCase: Provider<CheckOffIngredientUseCase>,
    private val uncheckIngredientUseCase: Provider<UncheckIngredientUseCase>,
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private var ingredients = listOf<ShoppingListIngredient>()
    private var recipes = listOf<Recipe>()
    private var removeRecipeIndex = 0

    val shoppingListUiState =
        getShoppingListUseCase
            .get()
            .invoke().combine(
                getRecipesInShoppingListUseCase
                    .get()
                    .invoke()
            ) { getShoppingListResult, getMappedRecipesResult ->
                ingredients = getShoppingListResult.data ?: emptyList()
                recipes = getMappedRecipesResult.data ?: emptyList()
                when (getShoppingListResult.status) {
                    SUCCESS -> {
                        ShoppingListUiState.Success(ingredients, recipes)
                    }
                    ERROR -> {
                        ShoppingListUiState.Error(
                            message = getShoppingListResult.message.toString() +
                                    getMappedRecipesResult.message.toString()
                        )
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RecipeDetailUiState.Loading
            )

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    private val _removeRecipeDialogState = MutableStateFlow(dismissed())
    val removeRecipeDialogState = _removeRecipeDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _removeRecipeDialogState.value
        )

    fun onCheckOffShoppingIngredient(index: Int) {
        viewModelScope.launch {
            val item = ingredients[index]
            // Mark the recipe as saving
            // Save the recipe
            checkIngredientUseCase.get().invoke(item).collect { resource ->
                when (resource.status) {
                    SUCCESS -> {}
                    ERROR -> {
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = "There was a problem checking off the ingredient in " +
                                        "your shopping list. Please try again."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onUncheckShoppingIngredient(index: Int) {
        viewModelScope.launch {
            val item = ingredients[index]
            // Save the recipe
            uncheckIngredientUseCase.get().invoke(item).collect { resource ->
                when (resource.status) {
                    SUCCESS -> {}
                    ERROR -> {
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = "There was a problem unchecking the ingredient in your " +
                                        "shopping list. Please try again."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onRemoveRecipe() {
        viewModelScope.launch {
            val item = recipes[removeRecipeIndex]
            when (removeRecipeInShoppingListUseCase.get().invoke(item).status) {
                SUCCESS -> {}
                ERROR -> {
                    _errorDialogState.update {
                        displayed(
                            title = "Error",
                            message = "There was a problem removing the recipe from your " +
                                    "shopping list. Please try again."
                        )
                    }
                }
            }
        }
    }

    fun onRemoveIngredient(index: Int) {
        viewModelScope.launch {
            val item = ingredients[index]
            when (removeIngredientUseCase.get().invoke(item).status) {
                SUCCESS -> {}
                ERROR -> {
                    _errorDialogState.update {
                        displayed(
                            title = "Error",
                            message = "There was a problem removing the ingredient from your " +
                                    "shopping list. Please try again."
                        )
                    }
                }
            }
        }
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }

    fun dismissRemoveRecipeDialog() {
        _removeRecipeDialogState.update { dismissed() }
    }

    fun showRemoveRecipeDialog(index: Int) {
        removeRecipeIndex = index
        _removeRecipeDialogState.update {
            displayed(
                title = "Are you sure?",
                message = "Are you sure you'd like to remove this recipe and all its " +
                        "ingredients from your shopping list?"
            )
        }
    }
}
