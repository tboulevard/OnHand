package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.PantryIngredient

interface IngredientSearchRepository {
    suspend fun searchIngredients(query: String): List<PantryIngredient>
}
