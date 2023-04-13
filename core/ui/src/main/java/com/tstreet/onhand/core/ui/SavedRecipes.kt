package com.tstreet.onhand.core.ui

sealed interface SavedRecipesUiState {

    object Loading : SavedRecipesUiState

    data class Success(
        val recipes: List<RecipeSearchItem>
    ) : SavedRecipesUiState

    data class Error(
        val message: String
    ) : SavedRecipesUiState
}
