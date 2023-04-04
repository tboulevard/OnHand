package com.tstreet.onhand.core.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tstreet.onhand.core.model.SaveableRecipe

sealed interface RecipeSearchUiState {

    object Loading : RecipeSearchUiState

    data class Success(
        val recipes: SnapshotStateList<SaveableRecipe>,
    ) : RecipeSearchUiState

    data class Error(
        val message: String
    ) : RecipeSearchUiState
}
