package com.tstreet.onhand.core.model

/**
 * High-level representation of a recipe, containing identifying information and ingredients
 * to make it. More detailed information to be modeled in [RecipeDetail], which with this class form
 * the complete information for a recipe in [FullRecipe].
 */
data class RecipePreview(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val usedIngredients: List<RecipeIngredient>,
    val missedIngredientCount: Int,
    val missedIngredients: List<RecipeIngredient>,
    val likes: Int,
    val isCustom: Boolean
)
