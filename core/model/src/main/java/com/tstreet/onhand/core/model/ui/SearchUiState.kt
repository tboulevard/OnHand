package com.tstreet.onhand.core.model.ui

sealed interface SearchUiState {
    data class Content(
        val ingredients: List<UiSearchIngredient>
    ) : SearchUiState

    object Empty : SearchUiState

    object Error : SearchUiState

    object Loading : SearchUiState
}