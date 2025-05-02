package com.tstreet.onhand.core.model.ui

sealed interface SavedRecipesUiState {

    data class Content(
        val recipes: List<RecipeWithSaveState>
    ) : SavedRecipesUiState

    object Empty : SavedRecipesUiState

    object Error : SavedRecipesUiState

    object Loading : SavedRecipesUiState
}
