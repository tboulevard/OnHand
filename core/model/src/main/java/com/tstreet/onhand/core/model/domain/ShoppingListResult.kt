package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.data.ShoppingListIngredient

sealed interface ShoppingListResult {

    data class Success(
        val ingredients: List<ShoppingListIngredient>
    ) : ShoppingListResult

    object Error : ShoppingListResult
    object Loading : ShoppingListResult
}