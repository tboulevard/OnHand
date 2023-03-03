package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for database containing all possible ingredients for the app to use.
 */
@Dao
interface IngredientCatalogDao {

    @Query("SELECT * FROM ingredient_catalog")
    suspend fun getAll(): List<IngredientCatalogEntity>

    @Query("SELECT * FROM ingredient_catalog WHERE name LIKE '%' || :searchText || '%'")
    suspend fun findByName(searchText: String): List<IngredientCatalogEntity>

}