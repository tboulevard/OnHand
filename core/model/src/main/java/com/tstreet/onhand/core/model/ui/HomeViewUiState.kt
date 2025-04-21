package com.tstreet.onhand.core.model.ui

sealed interface HomeViewUiState {

    object Loading : HomeViewUiState

    data class Content(
        val ingredients: List<UiPantryIngredient>
    ) : HomeViewUiState

    object Empty : HomeViewUiState
    object Error : HomeViewUiState
}