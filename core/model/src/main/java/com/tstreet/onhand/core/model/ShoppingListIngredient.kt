package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListIngredient(
    val name: String,
    val mappedRecipePreview: RecipePreview? = null,
    val isPurchased : Boolean
)
