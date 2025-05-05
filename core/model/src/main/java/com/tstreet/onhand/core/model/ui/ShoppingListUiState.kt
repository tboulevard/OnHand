package com.tstreet.onhand.core.model.ui

sealed interface ShoppingListUiState {
    object Loading : ShoppingListUiState

    data class Content(
        val ingredients: List<UiShoppingListIngredient>,
        val mappedRecipes: List<UiShoppingListRecipe>
    ) : ShoppingListUiState {

        fun screenContent() = listOf(
            UiShoppingListRowItem.Header(
                text = "Shopping List"
            ),
            UiShoppingListRowItem.Summary(
                numberOfRecipes = mappedRecipes.size,
                numberOfIngredients = ingredients.size
            ),
            UiShoppingListRowItem.MappedRecipes(
                recipePreviews = mappedRecipes
            ),
            UiShoppingListRowItem.Ingredients(
                ingredients = ingredients
            )
        )
    }

    object Error : ShoppingListUiState
}