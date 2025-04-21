package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.PantryIngredient

sealed interface IngredientSearchResult {

    data class Success(
        val ingredients: List<PantryIngredient>
    ) : IngredientSearchResult
    object Error : IngredientSearchResult
    object Loading : IngredientSearchResult
}