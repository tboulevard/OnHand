package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import kotlinx.coroutines.flow.Flow

/**
 * For accessing all possible ingredients for the app to use.
 */
@Dao
interface IngredientCatalogDao {

    // TODO: LIMIT of 6 entries returned hardcoded for now, make it dynamic later
    @Query("SELECT * FROM ingredient_catalog WHERE name LIKE '%' || :query || '%' LIMIT 6")
    suspend fun search(query: String): List<IngredientCatalogEntity>

    @Query("UPDATE ingredient_catalog SET inPantry = 1 WHERE name = :ingredientName AND inPantry = 0")
    suspend fun addToPantry(ingredientName: String) : Int

    @Query("UPDATE ingredient_catalog SET inPantry = 0 WHERE name = :ingredientName AND inPantry = 1")
    suspend fun removeFromPantry(ingredientName: String) : Int

    // NOTE: Because this wraps with Flow<>, any changes on the table level will trigger the
    // emission of a new value for all active subscribers
    // This function is a good candidate to wrap flow because we emit values over time (i.e.,
    // pantry items can change in UI multiple times)
    @Query("SELECT * FROM ingredient_catalog WHERE inPantry = 1")
    @Transaction
    fun getAllFromPantry(): Flow<List<IngredientCatalogEntity>>
}
