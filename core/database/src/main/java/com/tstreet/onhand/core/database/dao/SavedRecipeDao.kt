package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tstreet.onhand.core.database.model.SavedRecipeEntity

@Dao
interface SavedRecipeDao {

    @Insert
    suspend fun addRecipe(recipe: SavedRecipeEntity)

    @Query("DELETE FROM ingredient_catalog WHERE id = :id")
    suspend fun deleteRecipe(id: Int)
}