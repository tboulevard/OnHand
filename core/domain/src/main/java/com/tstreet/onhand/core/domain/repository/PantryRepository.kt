package com.tstreet.onhand.core.domain.repository

import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.data.PantryIngredient
import kotlinx.coroutines.flow.Flow

interface PantryRepository {

    /**
     * Adds the given [Ingredient] to pantry
     *
     * @return the number of rows updates in DB
     */
    suspend fun addIngredient(ingredient: Ingredient): Long

    /**
     * Removes the given [Ingredient] to pantry
     *
     * @return the number of rows updates in DB
     */
    suspend fun removeIngredient(ingredient: Ingredient): Int

    /**
     * Returns all [Ingredient]s in pantry.
     */
    suspend fun listPantry(): List<PantryIngredient>

    /**
     * Returns all [Ingredient]s in pantry and observes & emits all changes to the underlying
     * datastore.
     */
    fun listPantryFlow(): Flow<List<PantryIngredient>>

    /**
     * Returns all [Ingredient]s in pantry matching the [category].
     */
    suspend fun listPantryByCategory(category: IngredientCategory): List<PantryIngredient>

    /**
     * Returns all [Ingredient]s in pantry matching the [category].
     */
    fun listPantryByCategoryFlow(category: IngredientCategory): Flow<List<PantryIngredient>>

    /**
     * Given a list of [Ingredient]s, returns the subset of those ingredients that are
     * currently in the pantry.
     */
    suspend fun listPantry(ingredients: List<Ingredient>): List<PantryIngredient>
}
