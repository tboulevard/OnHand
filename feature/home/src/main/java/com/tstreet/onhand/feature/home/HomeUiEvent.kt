package com.tstreet.onhand.feature.home

import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2

sealed class HomeUiEvent {
    object Idle : HomeUiEvent()

    sealed class ButtonClick : HomeUiEvent() {
        data class CategoryFilter(val category: SelectableIngredientCategory) : ButtonClick()
        data class AddToPantry(val ingredient: UiPantryIngredientV2) : ButtonClick()
        data class RemoveFromPantry(val ingredient: UiPantryIngredientV2) : ButtonClick()
    }

    sealed class Navigation : HomeUiEvent() {
        data object IngredientSearch : Navigation()
    }

    data class ShowSnackbar(val message: String) : HomeUiEvent()
}