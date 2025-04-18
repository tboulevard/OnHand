package com.tstreet.onhand.core.model.ui

sealed interface RecipeSearchUiState {

    data class Content(
        val recipes: List<RecipeWithSaveState>,
    ) : RecipeSearchUiState

    object Empty : RecipeSearchUiState

    object Error : RecipeSearchUiState

    object Loading : RecipeSearchUiState
}
