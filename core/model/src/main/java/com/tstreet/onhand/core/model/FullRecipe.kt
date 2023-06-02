package com.tstreet.onhand.core.model

/**
 * Composite of [RecipePreview] and [RecipeDetail] - All information we can gather on a given recipe.
 */
data class FullRecipe(
    val preview: RecipePreview,
    val detail: RecipeDetail
)
