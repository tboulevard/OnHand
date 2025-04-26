package com.tstreet.onhand.core.model.ui

import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.ShoppingListIngredient

sealed interface ShoppingListItem {

    data class Header(
        val text: String
    ) : ShoppingListItem

    data class Summary(
        private val numberOfRecipes: Int,
        private val numberOfIngredients: Int
    ) : ShoppingListItem {

        val text = "$numberOfRecipes recipes - $numberOfIngredients items"
    }

    data class MappedRecipes(
        val recipePreviews: List<RecipePreview>
    ) : ShoppingListItem

    data class Ingredients(
        val ingredients: List<ShoppingListIngredient>
    ) : ShoppingListItem
}