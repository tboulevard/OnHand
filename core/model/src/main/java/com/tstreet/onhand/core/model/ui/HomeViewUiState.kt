package com.tstreet.onhand.core.model.ui

sealed interface PantryUiState {
    data class Content(
        val ingredients: List<UiPantryIngredient>
    ) : PantryUiState

    object Empty : PantryUiState

    object Error : PantryUiState

    object Loading : PantryUiState
}