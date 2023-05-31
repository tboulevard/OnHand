package com.tstreet.onhand.core.ui

import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.RecipeDetail

sealed interface RecipeDetailUiState {

    object Loading : RecipeDetailUiState

    data class Success(
        val recipePreview: RecipePreview?,
        val detail : RecipeDetail?
    ) : RecipeDetailUiState

    data class Error(
        val message: String
    ) : RecipeDetailUiState
}
