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
import com.tstreet.onhand.core.ui.ErrorDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.ErrorDialogState.Companion.displayed
import com.tstreet.onhand.core.ui.RecipeDetailUiState
import com.tstreet.onhand.core.ui.ShoppingListUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class ShoppingListViewModel @Inject constructor(
    getShoppingListUseCase: Provider<GetShoppingListUseCase>,
    getRecipesInShoppingListUseCase: Provider<GetRecipesInShoppingListUseCase>,
    private val removeRecipeInShoppingListUseCase: Provider<RemoveRecipeInShoppingListUseCase>,
    private val checkIngredientUseCase: Provider<CheckOffIngredientUseCase>,
    private val uncheckIngredientUseCase: Provider<UncheckIngredientUseCase>,
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private var _shoppingList = mutableStateListOf<ShoppingListIngredient>()
    private var _mappedRecipes = mutableStateListOf<Recipe>()

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
                        _shoppingList =
                            getShoppingListResult.data?.toMutableStateList() ?: mutableStateListOf()
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

    // TODO: create state object
    private val _removeRecipeConfirmationDialogState = MutableStateFlow(false)
    val removeRecipeDialogState = _removeRecipeConfirmationDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _removeRecipeConfirmationDialogState.value
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
                                "There was a problem checking off the ingredient in your " +
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
                                "There was a problem unchecking the ingredient in your " +
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

    fun onRemoveRecipe(index: Int) {
        viewModelScope.launch {
            val item = _mappedRecipes[index]
            println("[OnHand] Removing recipe at index=$item")

            when (removeRecipeInShoppingListUseCase.get().invoke(item).status) {
                SUCCESS -> {
                    _mappedRecipes.remove(item)
                    _shoppingList.removeIf {
                        val mappedRecipeTitle = it.mappedRecipe?.title
                        val itemTitle = item.title
                        val condition = it.mappedRecipe?.title == item.title
                        condition
                    }
                }
                ERROR -> {
                    _errorDialogState.update {
                        displayed(
                            "There was a problem removing the recipe from your shopping list. " +
                                    "Please try again."
                        )
                    }
                }
            }
        }
    }

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }

    // TODO cleanup below by wrapping in state object
    fun dismissRemoveRecipeConfirmationDialog() {
        _removeRecipeConfirmationDialogState.update { false }
    }

    fun showRemoveRecipeConfirmationDialog() {
        _removeRecipeConfirmationDialogState.update { true }
    }
}
