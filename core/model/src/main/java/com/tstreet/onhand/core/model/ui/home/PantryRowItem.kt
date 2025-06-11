package com.tstreet.onhand.core.model.ui.home

import com.tstreet.onhand.core.model.data.IngredientCategory

sealed interface PantryRowItem {

    class Header(
        val category: IngredientCategory
    ) : PantryRowItem

    class Ingredient(
        val ingredient: UiPantryIngredientV2
    ) : PantryRowItem
}