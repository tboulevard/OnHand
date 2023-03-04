package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity

/**
 * For accessing all possible ingredients for the app to use.
 */
@Dao
interface IngredientCatalogDao {

    @Query("SELECT * FROM ingredient_catalog")
    suspend fun getAll(): List<IngredientCatalogEntity>

    // TODO: LIMIT of 6 entries returned hardcoded for now, make it dynamic later
    @Query("SELECT * FROM ingredient_catalog WHERE name LIKE '%' || :query || '%' LIMIT 6")
    suspend fun search(query: String): List<IngredientCatalogEntity>

}