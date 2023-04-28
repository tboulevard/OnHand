package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.flow.Flow

interface PantryRepository {

    suspend fun addIngredient(ingredient: Ingredient)

    suspend fun removeIngredient(ingredient: Ingredient)

    fun listPantry() : Flow<List<PantryIngredient>>
}
