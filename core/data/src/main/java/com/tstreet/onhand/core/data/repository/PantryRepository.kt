package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Ingredient
import kotlinx.coroutines.flow.Flow

interface PantryRepository {
    //TODO: Add return value representing success/failure state of adding/removing from DB
    suspend fun addIngredient(ingredient: Ingredient)

    suspend fun removeIngredient(ingredient: Ingredient)

    fun listPantry() : Flow<List<Ingredient>>
}
