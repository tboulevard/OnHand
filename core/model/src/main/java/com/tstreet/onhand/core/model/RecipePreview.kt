package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

/**
 * General purpose representation of a recipe, containing identifying information and ingredients
 * to make it.
 */
@Serializable
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

/**
 * A [RecipePreview] with additional information for whether it is saved locally (i.e. in DB).
 */
data class SaveableRecipe(
    val recipePreview: RecipePreview,
    val isSaved: Boolean = false
)

/**
 * Contains all the components we gather from the user when creating a custom recipe. Class
 * exists so that all information we gather for a custom recipe must be present to create a full
 * recipe. Shares many of the same fields as [RecipePreview].
 */
@Serializable
open class CustomRecipeInput(
    val recipeTitle: String,
    val recipeImage: String,
    val recipeImageType: String,
    val ingredients: List<RecipeIngredient>,
    val instructions: String? = null
)

/**
 * Composite of [RecipePreview] and [RecipeDetail] - All information we can gather on a given recipe.
 */
data class FullRecipe(
    val preview: RecipePreview,
    val detail: RecipeDetail
)
