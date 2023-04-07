package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tstreet.onhand.core.database.model.RecipeSearchCacheEntity

@Dao
interface RecipeSearchCacheDao {

    @Insert
    suspend fun addRecipeSearchResult(recipes: List<RecipeSearchCacheEntity>)

    @Query("SELECT * FROM recipe_search_cache")
    suspend fun getRecipeSearchResult() : List<RecipeSearchCacheEntity>

    @Query("DELETE FROM recipe_search_cache")
    suspend fun clear()
}
