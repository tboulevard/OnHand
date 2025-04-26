package com.tstreet.onhand.core.model.ui

sealed interface SavedRecipesUiState {

    object Loading : SavedRecipesUiState

    data class Success(
        val recipes: List<RecipeWithSaveState>
    ) : SavedRecipesUiState

    data class Error(
        val message: String
    ) : SavedRecipesUiState
}
