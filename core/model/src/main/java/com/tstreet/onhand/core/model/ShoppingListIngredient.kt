package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListIngredient(
    val name: String,
    val mappedRecipe: Recipe? = null,
    val isPurchased : Boolean
)
