package com.tstreet.onhand.core.model.ui


sealed interface UiShoppingListRowItem {

    data class Header(
        val text: String
    ) : UiShoppingListRowItem

    data class Summary(
        private val numberOfRecipes: Int,
        private val numberOfIngredients: Int
    ) : UiShoppingListRowItem {

        val text = "$numberOfRecipes recipes - $numberOfIngredients items"
    }

    data class MappedRecipes(
        val recipePreviews: List<UiShoppingListRecipe>
    ) : UiShoppingListRowItem

    data class Ingredients(
        val ingredients: List<UiShoppingListIngredient>
    ) : UiShoppingListRowItem
}