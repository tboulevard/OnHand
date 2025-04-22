package com.tstreet.onhand.core.model

data class ShoppingListIngredient(
    val name: String,
    val mappedRecipePreview: RecipePreview? = null,
    val isPurchased: Boolean
)
