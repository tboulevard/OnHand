package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.tstreet.onhand.core.database.model.IngredientEntity
import kotlinx.coroutines.flow.Flow

/**
 * For accessing set of mock ingredients only.
 */
@Dao
interface IngredientDao {

    // TODO: Filtering, paging, etc.
    @Query("SELECT * FROM ingredient_catalog WHERE name LIKE '%' || :query || '%' LIMIT 10")
    fun getIngredients(query: String): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredient_catalog ORDER BY RANDOM() LIMIT 10")
    fun getRandomIngredients(): Flow<List<IngredientEntity>>
}