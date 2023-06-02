package com.tstreet.onhand.core.ui

import com.tstreet.onhand.core.model.FullRecipe

sealed interface RecipeDetailUiState {

    object Loading : RecipeDetailUiState

    data class Success(
        val recipe: FullRecipe?
    ) : RecipeDetailUiState

    data class Error(
        val message: String
    ) : RecipeDetailUiState
}
