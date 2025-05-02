package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState

sealed interface RecipeSearchResult {

    data class Success(
        val recipes: List<RecipePreviewWithSaveState>
    ) : RecipeSearchResult
    object Error : RecipeSearchResult
    object Loading : RecipeSearchResult
}