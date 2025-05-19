package com.tstreet.onhand.feature.shoppinglist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.DEFAULT
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.usecase.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.usecase.pantry.RemoveFromPantryUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.AddToShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.GetShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.RemoveIngredientUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.RemoveRecipeInShoppingListUseCase
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.displayed
import com.tstreet.onhand.core.model.ui.ShoppingListUiState
import com.tstreet.onhand.core.model.ui.UiShoppingListIngredient
import com.tstreet.onhand.core.model.ui.UiShoppingListRecipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class ShoppingListViewModel @Inject constructor(
    getShoppingListUseCase: Provider<GetShoppingListUseCase>,
    private val addToPantryUseCase: Provider<AddToPantryUseCase>,
    private val addToShoppingListUseCase: Provider<AddToShoppingListUseCase>,
    private val removeIngredientUseCase: Provider<RemoveIngredientUseCase>,
    private val removeRecipeInShoppingListUseCase: Provider<RemoveRecipeInShoppingListUseCase>,
    private val removeFromPantryUseCase: Provider<RemoveFromPantryUseCase>,
    private val mapper: ShoppingListUiStateMapper,
    @Named(DEFAULT) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    init {
        Log.d("[OnHand]", "${this.javaClass.simpleName} created")
    }

    val shoppingListUiState =
        getShoppingListUseCase
            .get()
            .invoke()
            .map {
                mapper.mapShoppingListResultToUi(it)
            }.flowOn(dispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ShoppingListUiState.Loading
            )

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    fun onCheckOffShoppingIngredient(item: UiShoppingListIngredient) {
        viewModelScope.launch {
            val result = addToPantryUseCase.get().invoke(item.ingredient.ingredient)

            when (result.status) {
                SUCCESS -> {
                    item.isChecked.value = true
                }

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

    fun onUncheckShoppingIngredient(item: UiShoppingListIngredient) {
        viewModelScope.launch {
            val result = removeFromPantryUseCase.get().invoke(item.ingredient.ingredient)

            when (result.status) {
                SUCCESS -> {
                    item.isChecked.value = false
                }

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

    fun onRemoveRecipe(item: UiShoppingListRecipe) {
        viewModelScope.launch {
            when (removeRecipeInShoppingListUseCase.get().invoke(item.recipe).status) {
                SUCCESS -> {
                    item.isInCart.value = false

                    // Remove all ingredients in the recipe too
                    // TODO: Make a bulk operation in the future
                    for (ingredient in item.ingredients) {
                        onRemoveIngredient(ingredient)
                    }
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

    fun onUndoRemoveRecipe(item: UiShoppingListRecipe) {
        viewModelScope.launch {
            when (addToShoppingListUseCase.get()
                .addShoppingListIngredients(
                    item.ingredients.map { it.ingredient },
                    item.recipe
                ).status) {
                SUCCESS -> {
                    item.isInCart.value = true

                    // Reflect that all ingredients were added back too
                    // TODO: Make a bulk operation in the future
                    for (ingredient in item.ingredients) {
                        ingredient.isInCart.value = true
                    }
                }

                ERROR -> {
                    _errorDialogState.update {
                        displayed(
                            title = "Error",
                            message = "There was a problem re-adding the recipe to your " +
                                    "shopping list. Please try again."
                        )
                    }
                }
            }
        }
    }

    fun onRemoveIngredient(item: UiShoppingListIngredient) {
        viewModelScope.launch {
            when (removeIngredientUseCase.get().invoke(item.ingredient).status) {
                SUCCESS -> {
                    item.isInCart.value = false
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

    fun onAddIngredient(item: UiShoppingListIngredient) {
        viewModelScope.launch {
            when (addToShoppingListUseCase.get()
                .addShoppingListIngredients(listOf(item.ingredient)).status) {
                SUCCESS -> {
                    item.isInCart.value = true
                }

                ERROR -> {
                    _errorDialogState.update {
                        displayed(
                            title = "Error",
                            message = "There was a problem adding the ingredient from your " +
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
}
