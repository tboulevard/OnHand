package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.model.data.Ingredient
import kotlinx.coroutines.flow.Flow

interface PantryRepository {

    /**
     * Adds the given [Ingredient] to pantry
     *
     * @return the number of rows updates in DB
     */
    suspend fun addIngredient(ingredient: Ingredient) : Long

    /**
     * Removes the given [Ingredient] to pantry
     *
     * @return the number of rows updates in DB
     */
    suspend fun removeIngredient(ingredient: Ingredient) : Int

    /**
     * Emits [Ingredient]s in pantry each time there's an update to the underlying
     * table.
     */
    suspend fun listPantry() : List<Ingredient>

    suspend fun listPantry(ingredients: List<Ingredient>): List<Ingredient>
}
