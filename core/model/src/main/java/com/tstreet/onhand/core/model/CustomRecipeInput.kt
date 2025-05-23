package com.tstreet.onhand.core.model

import com.tstreet.onhand.core.model.data.Ingredient

/**
 * Contains all the components we gather from the user when creating a custom recipe. Class
 * exists so that all information we gather for a custom recipe must be present to create a full
 * recipe. Shares many of the same fields as [RecipePreview].
 */
class CustomRecipeInput(
    val recipeTitle: String,
    val recipeImage: String,
    val recipeImageType: String,
    val ingredients: List<Ingredient>,
    val instructions: String? = null
)
