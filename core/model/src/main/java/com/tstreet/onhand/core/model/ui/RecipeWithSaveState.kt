package com.tstreet.onhand.core.model.ui

import com.tstreet.onhand.core.model.RecipePreview

// Recipe wrapped in save state to allow the view model to toggle it
data class RecipeWithSaveState(
    val recipePreview: RecipePreview,
    val recipeSaveState: RecipeSaveState
)