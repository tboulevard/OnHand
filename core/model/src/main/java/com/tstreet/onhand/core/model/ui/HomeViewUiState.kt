package com.tstreet.onhand.core.model.ui

import com.tstreet.onhand.core.model.UiPantryIngredient

sealed interface HomeViewUiState {

    object Loading : HomeViewUiState

    data class Content(
        val ingredients: List<UiPantryIngredient>
    ) : HomeViewUiState

    object Empty : HomeViewUiState
    object Error : HomeViewUiState
}