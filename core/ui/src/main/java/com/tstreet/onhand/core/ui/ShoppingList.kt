package com.tstreet.onhand.core.ui

import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient

sealed interface ShoppingListUiState {

    object Loading : ShoppingListUiState

    data class Success(
        val ingredients: List<ShoppingListIngredient>,
        val recipes: List<Recipe>
    ) : ShoppingListUiState

    data class Error(
        val message: String
    ) : ShoppingListUiState
}
