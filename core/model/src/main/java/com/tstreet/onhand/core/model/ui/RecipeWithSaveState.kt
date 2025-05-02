package com.tstreet.onhand.core.model.ui

import androidx.compose.runtime.MutableState
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.Ingredient

// Recipe wrapped in save state to allow the view model to toggle it
data class RecipeWithSaveState(
    val preview: RecipePreview,
    val saveState: MutableState<RecipeSaveState>,
    val ingredientState: MutableState<IngredientAvailability>,
    val missingIngredientsInCart: MutableState<List<Ingredient>>
)

enum class RecipeSaveState {
    SAVED,
    NOT_SAVED,
    LOADING
}

enum class IngredientAvailability {
    MISSING_INGREDIENTS,
    ALL_INGREDIENTS_AVAILABLE
}