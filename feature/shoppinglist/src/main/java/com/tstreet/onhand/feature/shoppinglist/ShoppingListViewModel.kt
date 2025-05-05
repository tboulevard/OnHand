package com.tstreet.onhand.feature.shoppinglist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.usecase.shoppinglist.CheckOffIngredientUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.GetShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.RemoveIngredientUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.RemoveRecipeInShoppingListUseCase
import com.tstreet.onhand.core.domain.usecase.shoppinglist.UncheckIngredientUseCase
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
    private val removeIngredientUseCase: Provider<RemoveIngredientUseCase>,
    private val removeRecipeInShoppingListUseCase: Provider<RemoveRecipeInShoppingListUseCase>,
    private val checkIngredientUseCase: Provider<CheckOffIngredientUseCase>,
    private val uncheckIngredientUseCase: Provider<UncheckIngredientUseCase>,
    private val mapper: ShoppingListUiStateMapper,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
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
            }.flowOn(ioDispatcher)
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

    private val _removeRecipeDialogState = MutableStateFlow(dismissed())
    val removeRecipeDialogState = _removeRecipeDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _removeRecipeDialogState.value
        )

    fun onCheckOffShoppingIngredient(item: UiShoppingListIngredient) {
        viewModelScope.launch {
            checkIngredientUseCase.get().invoke(item.ingredient).collect { resource ->
                when (resource.status) {
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
    }

    fun onUncheckShoppingIngredient(item: UiShoppingListIngredient) {
        viewModelScope.launch {
            // Save the recipe
            uncheckIngredientUseCase.get().invoke(item.ingredient).collect { resource ->
                when (resource.status) {
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
    }

    fun onRemoveRecipe(item: UiShoppingListRecipe) {
        viewModelScope.launch {
            when (removeRecipeInShoppingListUseCase.get().invoke(item.recipePreview).status) {
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

    fun onRemoveIngredient(item: UiShoppingListIngredient) {
        viewModelScope.launch {
            when (removeIngredientUseCase.get().invoke(item.ingredient).status) {
                SUCCESS -> {

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

    fun showRemoveRecipeDialog() {
        _removeRecipeDialogState.update {
            displayed(
                title = "Are you sure?",
                message = "Are you sure you'd like to remove this recipe and all its " +
                        "ingredients from your shopping list?"
            )
        }
    }
}
