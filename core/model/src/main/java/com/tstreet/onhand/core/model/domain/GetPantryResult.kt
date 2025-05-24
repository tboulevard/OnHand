package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.data.PantryIngredient

sealed interface GetPantryResult {

    data class Success(
        val ingredients: List<PantryIngredient>
    ) : GetPantryResult

    object Error : GetPantryResult
    object Loading : GetPantryResult
}