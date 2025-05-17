package com.tstreet.onhand.core.model.data

import com.tstreet.onhand.core.model.RecipePreview

/**
 * A [RecipePreview] with additional information for whether it is saved locally (i.e. in DB).
 */
data class RecipePreviewWithSaveState(
    val preview: RecipePreview,
    val isSaved: Boolean = false,
    val ingredientsMissingButInShoppingList: List<Ingredient>
)