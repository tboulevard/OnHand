package com.tstreet.onhand.core.ui

import com.tstreet.onhand.core.model.Recipe

sealed interface RecipeDetailUiState {

    object Loading : RecipeDetailUiState

    data class Success(
        val recipe: Recipe
    ) : RecipeDetailUiState

    data class Error(
        val message: String
    ) : RecipeDetailUiState
}
