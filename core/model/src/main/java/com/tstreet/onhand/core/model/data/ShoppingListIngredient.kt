package com.tstreet.onhand.core.model.data

import com.tstreet.onhand.core.model.RecipePreview

data class ShoppingListIngredient(
    val ingredient: Ingredient,
    val mappedRecipePreview: RecipePreview? = null,
    val inPantry: Boolean = false
)