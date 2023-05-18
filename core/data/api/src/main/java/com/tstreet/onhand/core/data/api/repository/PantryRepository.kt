package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.PantryIngredient
import kotlinx.coroutines.flow.Flow

interface PantryRepository {

    /**
     * Adds the given [Ingredient] to pantry by marking inPantry=true
     *
     * @return the number of rows updates in DB
     */
    suspend fun addIngredient(ingredient: Ingredient) : Int

    /**
     * Removes the given [Ingredient] to pantry by marking inPantry=false
     *
     * @return the number of rows updates in DB
     */
    suspend fun removeIngredient(ingredient: Ingredient) : Int

    /**
     * Emits [PantryIngredient]s where inPantry=true each time there's an update to the underlying
     * table.
     */
    fun listPantry() : Flow<List<PantryIngredient>>
}
