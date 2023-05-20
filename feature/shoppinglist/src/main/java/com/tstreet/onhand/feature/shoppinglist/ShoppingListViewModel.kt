package com.tstreet.onhand.feature.shoppinglist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
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

    private var _shoppingList = mutableStateListOf<ShoppingListIngredient>()
    private var _mappedRecipes = mutableStateListOf<Recipe>()
    private var removeRecipeIndex = 0

    val shoppingListUiState =
        getShoppingListUseCase
            .get()
            .invoke().combine(
                getRecipesInShoppingListUseCase
                    .get()
                    .invoke()
            ) { getShoppingListResult, getMappedRecipesResult ->
                when (getShoppingListResult.status) {
                    SUCCESS -> {
                        // TODO: Log analytics if data is null somehow. We fallback to emitting an
                        //  empty list.
                        _shoppingList = getShoppingListResult.data?.toMutableStateList()
                            ?: mutableStateListOf()
                        _mappedRecipes = getMappedRecipesResult.data?.toMutableStateList()
                            ?: mutableStateListOf()
                        ShoppingListUiState.Success(_shoppingList, _mappedRecipes)
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
            val item = _shoppingList[index]
            val isPurchased = item.isPurchased
            // Mark the recipe as saving
            _shoppingList[index] = item.copy(isPurchased = isPurchased)
            // Save the recipe
            checkIngredientUseCase.get().invoke(item).collect { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        _shoppingList[index] = item.copy(
                            isPurchased = true
                        )
                    }
                    ERROR -> {
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = "There was a problem checking off the ingredient in " +
                                        "your shopping list. Please try again."
                            )
                        }
                        // Retain the previous save state on error
                        _shoppingList[index] = item.copy(
                            isPurchased = isPurchased
                        )
                    }
                }
            }
        }
    }

    fun onUncheckShoppingIngredient(index: Int) {
        viewModelScope.launch {
            val item = _shoppingList[index]
            val isPurchased = item.isPurchased
            // Mark the recipe as saving
            _shoppingList[index] = item.copy(isPurchased = isPurchased)
            // Save the recipe
            uncheckIngredientUseCase.get().invoke(item).collect { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        _shoppingList[index] = item.copy(
                            isPurchased = false
                        )
                    }
                    ERROR -> {
                        _errorDialogState.update {
                            displayed(
                                title = "Error",
                                message = "There was a problem unchecking the ingredient in your " +
                                        "shopping list. Please try again."
                            )
                        }
                        // Retain the previous save state on error
                        _shoppingList[index] = item.copy(
                            isPurchased = isPurchased
                        )
                    }
                }
            }
        }
    }

    fun onRemoveRecipe() {
        viewModelScope.launch {
            val item = _mappedRecipes[removeRecipeIndex]
            when (removeRecipeInShoppingListUseCase.get().invoke(item).status) {
                SUCCESS -> {
                    _mappedRecipes.remove(item)
                    _shoppingList.removeIf { it.mappedRecipe?.title == item.title }
                }
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
            val item = _shoppingList[index]
            when (removeIngredientUseCase.get().invoke(item).status) {
                SUCCESS -> {
                    _shoppingList.removeAt(index)
                }
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
