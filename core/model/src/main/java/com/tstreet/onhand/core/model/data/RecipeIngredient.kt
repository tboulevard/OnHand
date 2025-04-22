package com.tstreet.onhand.core.model.data

/**
 * Representation for an ingredient as part of a recipe - includes information about how much
 * of that ingredient is needed for the recipe, etc.
 */
data class RecipeIngredient(
    val ingredient: Ingredient,
    val image: String? = null,
    val amount: Double,
    val unit: String
)