package com.tstreet.onhand.core.model.ui

import androidx.compose.runtime.MutableState
import com.tstreet.onhand.core.model.RecipePreview

// Recipe wrapped in save state to allow the view model to toggle it
data class RecipeWithSaveState(
    val preview: RecipePreview,
    val saveState: MutableState<RecipeSaveState>,
    val ingredientPantryState: MutableState<IngredientAvailability>,
    val ingredientShoppingCartState: MutableState<IngredientAvailability>
) {
    fun shoppingCartSatisfiesIngredients() =
        ingredientShoppingCartState.value == IngredientAvailability.ALL_INGREDIENTS_AVAILABLE

    fun pantrySatisfiesIngredients() =
        ingredientPantryState.value == IngredientAvailability.ALL_INGREDIENTS_AVAILABLE
}

enum class RecipeSaveState {
    SAVED,
    NOT_SAVED,
    LOADING
}

// Reflects ingredients we have vs ingredients in pantry
enum class IngredientAvailability {
    MISSING_INGREDIENTS,
    ALL_INGREDIENTS_AVAILABLE
}