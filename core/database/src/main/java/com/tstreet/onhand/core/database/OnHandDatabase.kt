package com.tstreet.onhand.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.dao.RecipeSearchCacheDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.dao.ShoppingListDao
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import com.tstreet.onhand.core.database.model.RecipeSearchCacheEntity
import com.tstreet.onhand.core.database.model.SavedRecipeEntity
import com.tstreet.onhand.core.database.model.ShoppingListEntity

@Database(
    entities = [
        IngredientCatalogEntity::class,
        SavedRecipeEntity::class,
        RecipeSearchCacheEntity::class,
        ShoppingListEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class OnHandDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientCatalogDao

    abstract fun savedRecipeDao(): SavedRecipeDao

    abstract fun recipeSearchCacheDao(): RecipeSearchCacheDao

    abstract fun shoppingListDao(): ShoppingListDao
}
