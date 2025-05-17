package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState

sealed interface SavedRecipesResult {

    data class Success(
        val recipes: List<RecipePreviewWithSaveState>
    ) : SavedRecipesResult
    object Error : SavedRecipesResult
    object Loading : SavedRecipesResult
}