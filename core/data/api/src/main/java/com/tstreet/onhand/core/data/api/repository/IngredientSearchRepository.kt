package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.model.PantryIngredient

interface IngredientSearchRepository {

    suspend fun searchIngredients(query: String): List<PantryIngredient>
}
