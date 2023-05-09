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

    val shoppingListUiState = getShoppingListUseCase
        .get()
        .invoke()
        .map { resource ->
            when (resource.status) {
                SUCCESS -> {
                    // TODO: Log analytics if data is null somehow. We fallback to emitting an
                    //  empty list.
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

    private val _errorDialogState = MutableStateFlow(dismissed())
    val errorDialogState = _errorDialogState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _errorDialogState.value
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
                            ErrorDialogState.displayed(
                                message = resource.message.toString()
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
                            ErrorDialogState.displayed(
                                message = resource.message.toString()
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
