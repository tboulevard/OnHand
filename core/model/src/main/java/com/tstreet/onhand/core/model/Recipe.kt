package com.tstreet.onhand.core.model

/**
 * General purpose representation of a recipe, containing identifying information and ingredients
 * to make it.
 */
class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val usedIngredients: List<RecipeIngredient>,
    val missedIngredientCount: Int,
    val missedIngredients: List<RecipeIngredient>,
    val likes: Int
)

/**
 * A [Recipe] with additional information for whether it is saved locally (i.e. in DB).
 */
data class SaveableRecipe(
    val recipe: Recipe,
    val isSaved: Boolean = false,
)
