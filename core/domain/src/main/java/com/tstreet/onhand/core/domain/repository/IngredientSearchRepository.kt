package com.tstreet.onhand.core.domain.repository

import com.tstreet.onhand.core.model.data.Ingredient
import kotlinx.coroutines.flow.Flow

interface IngredientSearchRepository {

    fun searchIngredients(query: String): Flow<List<Ingredient>>

    fun mostPopularIngredients(): Flow<List<Ingredient>>
}
