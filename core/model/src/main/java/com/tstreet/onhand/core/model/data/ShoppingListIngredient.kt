package com.tstreet.onhand.core.model.data

import com.tstreet.onhand.core.model.RecipePreview

data class ShoppingListIngredient(
    val name: String,
    val mappedRecipePreview: RecipePreview? = null,
    val isPurchased: Boolean
)