package com.tstreet.onhand.core.model

/**
 * A [RecipePreview] with additional information for whether it is saved locally (i.e. in DB).
 */
data class SaveableRecipePreview(
    val recipePreview: RecipePreview,
    val isSaved: Boolean = false
)
