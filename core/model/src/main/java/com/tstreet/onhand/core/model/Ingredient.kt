package com.tstreet.onhand.core.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

/**
 * External model representation for an Ingredient.
 */
@Stable
@Serializable
data class Ingredient(
    val id: Int,
    val name: String
)

/**
 * Ingredient that tells us whether its stored in the pantry.
 *
 * NOTE: For now, amounts/unit not stored
 */
data class PantryIngredient(
    val ingredient: Ingredient,
    val inPantry: Boolean = false,
)

/**
 * Representation for an ingredient as part of a recipe - includes information about how much
 * of that ingredient is needed for the recipe, etc.
 */
@Serializable
data class RecipeIngredient(
    val ingredient: Ingredient,
    val image: String? = null,
    val amount: Double,
    val unit: String
)
