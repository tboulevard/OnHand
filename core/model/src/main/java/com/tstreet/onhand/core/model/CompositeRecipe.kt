package com.tstreet.onhand.core.model

/**
 * Composite of [Recipe] and [RecipeDetail].
 *
 * Primarily used so sourceUrl and ingredients are on the same class.
 */
data class CompositeRecipe(
    // Recipe fields
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val missedIngredients: List<RecipeIngredient>,
    val missedIngredientCount: Int,
    val usedIngredients: List<RecipeIngredient>,
    val usedIngredientCount: Int,
    val likes: Int,

    // Recipe detail field
    val sourceUrl: String
)