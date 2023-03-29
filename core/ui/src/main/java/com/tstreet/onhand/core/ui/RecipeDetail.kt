package com.tstreet.onhand.core.ui

import com.tstreet.onhand.core.model.RecipeDetail

sealed interface RecipeDetailUiState {

    object Loading : RecipeDetailUiState

    data class Success(
        val recipeDetail: RecipeDetail
    ) : RecipeDetailUiState

    data class Error(
        val message: String
    ) : RecipeDetailUiState
}
