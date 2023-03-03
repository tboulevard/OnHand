package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity

/**
 * DAO for database containing all possible ingredients for the app to use.
 */
@Dao
interface IngredientCatalogDao {

    @Query("SELECT * FROM ingredientcatalogentity")
    fun getAll(): List<IngredientCatalogEntity>

    @Query("SELECT * FROM ingredientcatalogentity WHERE name LIKE :searchText")
    fun findByName(searchText: String): List<IngredientCatalogEntity>

}