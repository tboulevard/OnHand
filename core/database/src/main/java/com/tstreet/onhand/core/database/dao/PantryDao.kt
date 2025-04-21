package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tstreet.onhand.core.database.model.PantryEntity
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {

    /**
     * @return the row id of the newly inserted ingredient
     */
    @Insert(
        entity = PantryEntity::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun addToPantry(ingredient: PantryEntity): Long

    /**
     * @return the number of rows affected
     */
    @Delete(entity = PantryEntity::class)
    suspend fun removeFromPantry(ingredient: PantryEntity): Int

    @Query("SELECT * FROM pantry")
    fun getAllFromPantry(): Flow<List<PantryEntity>>

    @Query("SELECT * FROM pantry WHERE id IN (:ids)")
    suspend fun getPantryItemsWithIds(ids: List<Int>): List<PantryEntity>

}