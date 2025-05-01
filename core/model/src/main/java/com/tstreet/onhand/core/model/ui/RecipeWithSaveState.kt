package com.tstreet.onhand.core.model.ui

import androidx.compose.runtime.MutableState
import com.tstreet.onhand.core.model.RecipePreview

// Recipe wrapped in save state to allow the view model to toggle it
data class RecipeWithSaveState(
    val preview: RecipePreview,
    val saveState: MutableState<RecipeSaveState>
)

enum class RecipeSaveState {
    SAVED,
    NOT_SAVED,
    LOADING
}