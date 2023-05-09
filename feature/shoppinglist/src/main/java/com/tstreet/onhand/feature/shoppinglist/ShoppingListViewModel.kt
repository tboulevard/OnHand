package com.tstreet.onhand.feature.shoppinglist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS
import com.tstreet.onhand.core.domain.shoppinglist.GetShoppingListUseCase
import com.tstreet.onhand.core.domain.shoppinglist.CheckOffIngredientUseCase
import com.tstreet.onhand.core.domain.shoppinglist.UncheckIngredientUseCase
import com.tstreet.onhand.core.model.ShoppingListIngredient
import com.tstreet.onhand.core.ui.ErrorDialogState
import com.tstreet.onhand.core.ui.ErrorDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.RecipeDetailUiState
import com.tstreet.onhand.core.ui.ShoppingListUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class ShoppingListViewModel @Inject constructor(
    getShoppingListUseCase: Provider<GetShoppingListUseCase>,
    private val checkIngredientUseCase: Provider<CheckOffIngredientUseCase>,
    private val uncheckIngredientUseCase: Provider<UncheckIngredientUseCase>
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private var _shoppingList = mutableStateListOf<ShoppingListIngredient>()

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
        )

    val shoppingListUiState = getShoppingListUseCase
        .get()
        .invoke()
        .map { resource ->
            when (resource.status) {
                SUCCESS -> {
                    _shoppingList = resource.data?.toMutableStateList() ?: mutableStateListOf()
                    ShoppingListUiState.Success(_shoppingList)
                }
                ERROR -> {
                    ShoppingListUiState.Error(message = resource.message.toString())
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecipeDetailUiState.Loading
        )

    fun onCheckOffShoppingIngredient(index: Int) {
        viewModelScope.launch {
            val item = _shoppingList[index]
            val isPurchased = item.isPurchased
            // Mark the recipe as saving
            _shoppingList[index] = item.copy(isPurchased = isPurchased)
            // Save the recipe
            checkIngredientUseCase.get().invoke(item).collect {
                when (it) {
                    // When save is successful, update UI state
                    true -> {
                        _shoppingList[index] = item.copy(
                            isPurchased = true
                        )
                    }
                    else -> {
                        _errorDialogState.update {
                            ErrorDialogState.displayed(
                                message = "Unable to check off ingredient,  there was an error."
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
            uncheckIngredientUseCase.get().invoke(item).collect {
                when (it) {
                    // When save is successful, update UI state
                    true -> {
                        _shoppingList[index] = item.copy(
                            isPurchased = false
                        )
                    }
                    else -> {
                        _errorDialogState.update {
                            ErrorDialogState.displayed(
                                message = "Unable to uncheck ingredient, there was an error."
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

    fun dismissErrorDialog() {
        _errorDialogState.update { dismissed() }
    }
}
