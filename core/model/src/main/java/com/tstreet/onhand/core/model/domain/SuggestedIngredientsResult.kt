package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.data.PantryIngredient

sealed interface SuggestedIngredientsResult {

    data class Success(
        val ingredients: List<PantryIngredient>
    ) : SuggestedIngredientsResult
    object Error : SuggestedIngredientsResult
    object Loading : SuggestedIngredientsResult
}