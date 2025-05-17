package com.tstreet.onhand.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.tstreet.onhand.core.database.model.RecipeSearchCacheEntity

@Dao
abstract class RecipeSearchCacheDao {

    @Query("SELECT * FROM recipe_search_cache")
    abstract suspend fun getRecipeSearchResult(): List<RecipeSearchCacheEntity>

    @Transaction
    open suspend fun cacheRecipeSearchResult(recipes: List<RecipeSearchCacheEntity>) {
        clear()
        addRecipeSearchResult(recipes)
    }

    @Insert
    abstract suspend fun addRecipeSearchResult(recipes: List<RecipeSearchCacheEntity>)

    @Query("DELETE FROM recipe_search_cache")
    abstract suspend fun clear()

    @Query("SELECT * from recipe_search_cache WHERE id = :id")
    abstract suspend fun getRecipe(id: Int): RecipeSearchCacheEntity
}
