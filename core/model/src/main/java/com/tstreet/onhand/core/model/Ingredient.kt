package com.tstreet.onhand.core.model

import androidx.compose.runtime.Stable

/**
 * External model representation for an Ingredient.
 */
@Stable
data class Ingredient(
    val id: Int,
    val name: String,
    val inPantry: Boolean = false
)

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

/**
 * Ingredient model for search results.
 */
data class SearchIngredient(
    val id: Int,
    val name: String
)
