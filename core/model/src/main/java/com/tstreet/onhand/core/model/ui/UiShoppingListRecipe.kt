package com.tstreet.onhand.core.model.ui

import com.tstreet.onhand.core.model.RecipePreview

data class UiShoppingListRecipe(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val recipe: RecipePreview,
    val ingredients: List<UiShoppingListIngredient>
)