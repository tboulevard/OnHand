package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.flow.Flow

interface IngredientSearchRepository {

    fun searchIngredients(query: String): Flow<List<PantryIngredient>>
}
