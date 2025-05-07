package com.tstreet.onhand.core.model.ui

sealed interface ShoppingListUiState {
    object Loading : ShoppingListUiState

    data class Content(
        val recipesWithIngredients: List<UiShoppingListRecipe>
    ) : ShoppingListUiState {

        fun screenContent(): List<UiShoppingListRowItem> {
            val rowItems = mutableListOf<UiShoppingListRowItem>(
                UiShoppingListRowItem.Header(
                    text = "Shopping List"
                )
            )

            rowItems.addAll(recipesWithIngredients.map {
                UiShoppingListRowItem.RecipeIngredientGroup(
                    recipe = it
                )
            })

            return rowItems
        }
    }

    object Error : ShoppingListUiState
}