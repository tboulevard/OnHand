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
    val name: String,
    // TODO: Path only, refactor once we need images
    val childIngredient: ChildIngredient? = null,
)

/**
 * Representation for an ingredient in the pantry - for now, no amounts are stored.
 */
data class PantryIngredient(
    val ingredient: Ingredient,
    val inPantry : Boolean = false,
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
    val unit: String,
)
