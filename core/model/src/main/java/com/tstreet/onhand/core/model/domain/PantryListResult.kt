package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.data.PantryIngredient

sealed interface PantryListResult {

    data class Success(
        val ingredients: List<PantryIngredient>
    ) : PantryListResult
    object Error : PantryListResult
    object Loading : PantryListResult
}