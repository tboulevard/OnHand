package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tstreet.onhand.core.database.model.SavedRecipeEntity

@Dao
interface SavedRecipeDao {

    @Insert
    suspend fun addRecipe(recipe: SavedRecipeEntity)

    @Query("SELECT 1 from saved_recipes WHERE id = :id")
    suspend fun isRecipeSaved(id: Int): Int

    @Query("DELETE FROM saved_recipes WHERE id = :id")
    suspend fun deleteRecipe(id: Int)
}
