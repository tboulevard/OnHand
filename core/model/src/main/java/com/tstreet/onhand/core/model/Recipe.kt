package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

/**
 * General purpose representation of a recipe, containing identifying information and ingredients
 * to make it.
 */
@Serializable
class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val usedIngredients: List<RecipeIngredient>,
    val missedIngredientCount: Int,
    val missedIngredients: List<RecipeIngredient>,
    // TODO: figure out how to supply this later, on RecipeDetail and not saved by default
    //  with recipes from API
    val instructions: String? = null,
    val likes: Int
) : PartialRecipe(
    title, image, imageType, instructions, usedIngredients + missedIngredients
)

/**
 * A [Recipe] with additional information for whether it is saved locally (i.e. in DB).
 */
data class SaveableRecipe(
    val recipe: Recipe,
    val isSaved: Boolean = false,
)

/**
 * Contains all the components we gather from the user when creating a custom recipe. Class
 * exists so that all information we gather for a custom recipe must be present to create a full
 * recipe. Shares many of the same fields as [Recipe].
 */
@Serializable
open class PartialRecipe(
    // Prefixed with 'recipe' so parent Recipe can remain serializable...
    val recipeTitle: String,
    val recipeImage: String,
    val recipeImageType: String,
    val recipeInstructions: String?,
    val ingredients: List<RecipeIngredient>
)
