package com.tstreet.onhand.core.model.ui

sealed interface PantryUiState {
    data class Content(
        val ingredients: List<UiPantryIngredient>
    ) : PantryUiState

    object Empty : PantryUiState

    object Error : PantryUiState

    object Loading : PantryUiState

    /**
     * Placeholder state, not intended to do anything.
     */
    object None : PantryUiState
}

sealed interface SearchUiState {
    data class Content(
        val ingredients: List<UiPantryIngredient>
    ) : SearchUiState

    object Empty : SearchUiState

    object Error : SearchUiState

    object Loading : SearchUiState
}