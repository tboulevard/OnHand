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

    /**
     * A recipe and its ingredients.
     */
    data class RecipeIngredientGroup(
        val recipe: UiShoppingListRecipe
    ) : UiShoppingListRowItem

    /**
     * An ingredient that is not associated with a recipe.
     */
    data class StandaloneIngredient(
        val ingredients: List<UiShoppingListIngredient>
    ) : UiShoppingListRowItem
}