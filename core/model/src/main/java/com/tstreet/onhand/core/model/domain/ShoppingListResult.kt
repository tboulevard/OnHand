package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.ShoppingListIngredient

sealed interface ShoppingListResult {

    data class Success(
        val ingredients: List<ShoppingListIngredient>,
        val mappedRecipes: List<RecipePreview>
    ) : ShoppingListResult

    object Error : ShoppingListResult
    object Loading : ShoppingListResult
}