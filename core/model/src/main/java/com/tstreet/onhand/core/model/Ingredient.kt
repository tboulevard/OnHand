package com.tstreet.onhand.core.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf

/**
 * External model representation for an Ingredient.
 */
@Stable
data class Ingredient(
    val id: Int,
    val name: String
)

@Stable
data class PantryIngredient(
    val ingredient: Ingredient,
    val inPantry: Boolean = false
) {
    fun toggleInPantry() = copy(inPantry = !inPantry)
}

data class UiPantryIngredient(
    val ingredient: Ingredient,
    val inPantry: MutableState<Boolean> = mutableStateOf(false)
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
