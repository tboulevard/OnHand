package com.tstreet.onhand.core.ui

import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeDetail

sealed interface RecipeDetailUiState {

    object Loading : RecipeDetailUiState

    data class Success(
        val recipe: Recipe,
        val detail : RecipeDetail
    ) : RecipeDetailUiState

    data class Error(
        val message: String
    ) : RecipeDetailUiState
}
