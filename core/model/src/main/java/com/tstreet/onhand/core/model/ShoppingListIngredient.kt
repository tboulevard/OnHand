package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListIngredient(
    val name: String,
    // Because ingredients can be members of multiple recipes
    val mappedRecipes: List<Recipe>,
    val isPurchased : Boolean
)
