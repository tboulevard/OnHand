package com.tstreet.onhand.core.ui

import com.tstreet.onhand.core.model.Recipe

sealed interface RecipeSearchUiState {

    object Loading : RecipeSearchUiState

    data class Success(
        val recipes: List<Recipe>,
    ) : RecipeSearchUiState

    data class Error(
        val message: String
    ) : RecipeSearchUiState
}
