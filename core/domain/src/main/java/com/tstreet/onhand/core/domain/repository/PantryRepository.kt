package com.tstreet.onhand.core.domain.repository

import com.tstreet.onhand.core.model.data.Ingredient

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

    /**
     * Given a list of [Ingredient]s, returns the subset of those ingredients that are
     * currently in the pantry.
     */
    suspend fun listPantry(ingredients: List<Ingredient>): List<Ingredient>
}
