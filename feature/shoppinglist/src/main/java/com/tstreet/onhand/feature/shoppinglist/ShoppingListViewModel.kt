package com.tstreet.onhand.feature.shoppinglist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.usecase.shoppinglist.CheckOffIngredientUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.GetRecipesInShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.GetShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.RemoveIngredientUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.RemoveRecipeInShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.UncheckIngredientUseCase
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.ShoppingListIngredient
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import com.tstreet.onhand.core.model.ui.RecipeDetailUiState
import com.tstreet.onhand.core.model.ui.ShoppingListUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
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
        Log.d("[OnHand]", "${this.javaClass.simpleName} created")
    }

    private var ingredients = listOf<ShoppingListIngredient>()
    private var recipePreviews = listOf<RecipePreview>()
    private var removeRecipeIndex = 0
    private val errorDialogShown = AtomicBoolean(false)

    // NOTE: This flow is re-triggered if there's a change to the backing shopping_list table,
    //  so state is updated automatically. We retrieve a new list so both lists in their
    //  entirety recompose though. For now this approach is simple so we'll keep it so there are
    //  no perf issues
    val shoppingListUiState =
        getShoppingListUseCase
            .get()
            .invoke().combine(
                getRecipesInShoppingListUseCase
                    .get()
                    .invoke()
            ) { getShoppingListResult, getMappedRecipesResult ->
                ingredients = getShoppingListResult.data ?: emptyList()
                recipePreviews = getMappedRecipesResult.data ?: emptyList()
                when {
                    getShoppingListResult.status == SUCCESS &&
                            getMappedRecipesResult.status == SUCCESS -> {
                        ShoppingListUiState.Success(
                            mappedRecipePreviews = recipePreviews,
                            shoppingListIngredients = ingredients
                        )
                    }
                    else -> {
                        // So that edits to the shopping list don't re-show the error dialog
                        if (!errorDialogShown.getAndSet(true)) {
                            _errorDialogState.update {
                                displayed(
                                    title = "Error",
                                    message = "There was a problem retrieving some shopping list " +
                                            "contents. Showing partial results."
                                )
                            }
                        }
                        ShoppingListUiState.Error(
                            message = "getMappedRecipesResult = " +
                                    getMappedRecipesResult.message.toString() + ", " +
                                    "getShoppingListResult = " +
                                    getShoppingListResult.message.toString(),
                            mappedRecipePreviews = recipePreviews,
                            shoppingListIngredients = ingredients
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
            val item = recipePreviews[removeRecipeIndex]
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
