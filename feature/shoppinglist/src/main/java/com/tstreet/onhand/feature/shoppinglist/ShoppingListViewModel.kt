package com.tstreet.onhand.feature.shoppinglist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstreet.onhand.core.domain.GetShoppingListUseCase
import com.tstreet.onhand.core.domain.MarkShoppingIngredientUseCase
import com.tstreet.onhand.core.domain.UnmarkShoppingIngredientUseCase
import com.tstreet.onhand.core.model.ShoppingListIngredient
import com.tstreet.onhand.core.ui.RecipeDetailUiState
import com.tstreet.onhand.core.ui.ShoppingListUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class ShoppingListViewModel @Inject constructor(
    getShoppingListUseCase: Provider<GetShoppingListUseCase>,
    private val markShoppingIngredientUseCase: Provider<MarkShoppingIngredientUseCase>,
    private val unmarkShoppingIngredientUseCase: Provider<UnmarkShoppingIngredientUseCase>
) : ViewModel() {

    init {
        println("[OnHand] ${this.javaClass.simpleName} created")
    }

    private var _shoppingList = mutableStateListOf<ShoppingListIngredient>()

    val shoppingListUiState = getShoppingListUseCase
        .get()
        .invoke()
        .map {
            _shoppingList = it.toMutableStateList()
            // We pass the snapshot state list by reference to allow mutations within the ViewModel
            _shoppingList
        }
        .map(ShoppingListUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecipeDetailUiState.Loading
        )

    fun onMarkShoppingIngredient(index: Int) {
        viewModelScope.launch {
            val item = _shoppingList[index]
            val isPurchased = item.isPurchased
            // Mark the recipe as saving
            _shoppingList[index] = item.copy(isPurchased = isPurchased)
            // Save the recipe
            markShoppingIngredientUseCase.get().invoke(item).collect {
                when (it) {
                    // When save is successful, update UI state
                    true -> {
                        _shoppingList[index] = item.copy(
                            isPurchased = true
                        )
                    }
                    else -> {
                        // TODO: todo better error handling
                        println(
                            "[OnHand] Recipe save unsuccessful, there was an exception - " +
                                    "recipe not saved"
                        )
                        // Retain the previous save state on error
                        _shoppingList[index] = item.copy(
                            isPurchased = isPurchased
                        )
                    }
                }
            }
        }
    }

    fun onUnmarkShoppingIngredient(index: Int) {
        viewModelScope.launch {
            val item = _shoppingList[index]
            val isPurchased = item.isPurchased
            // Mark the recipe as saving
            _shoppingList[index] = item.copy(isPurchased = isPurchased)
            // Save the recipe
            unmarkShoppingIngredientUseCase.get().invoke(item).collect {
                when (it) {
                    // When save is successful, update UI state
                    true -> {
                        _shoppingList[index] = item.copy(
                            isPurchased = false
                        )
                    }
                    else -> {
                        // TODO: todo better error handling
                        println(
                            "[OnHand] Recipe save unsuccessful, there was an exception - " +
                                    "recipe not saved"
                        )
                        // Retain the previous save state on error
                        _shoppingList[index] = item.copy(
                            isPurchased = isPurchased
                        )
                    }
                }
            }
        }
    }
}
