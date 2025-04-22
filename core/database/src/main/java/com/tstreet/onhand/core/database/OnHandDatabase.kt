package com.tstreet.onhand.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tstreet.onhand.core.database.dao.IngredientDao
import com.tstreet.onhand.core.database.dao.PantryDao
import com.tstreet.onhand.core.database.dao.RecipeSearchCacheDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.dao.ShoppingListDao
import com.tstreet.onhand.core.database.model.IngredientEntity
import com.tstreet.onhand.core.database.model.PantryEntity
import com.tstreet.onhand.core.database.model.RecipeSearchCacheEntity
import com.tstreet.onhand.core.database.model.SavedRecipeEntity
import com.tstreet.onhand.core.database.model.ShoppingListEntity

@Database(
    entities = [
        IngredientEntity::class,
        PantryEntity::class,
        SavedRecipeEntity::class,
        RecipeSearchCacheEntity::class,
        ShoppingListEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class OnHandDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao

    abstract fun pantryDao(): PantryDao

    abstract fun savedRecipeDao(): SavedRecipeDao

    abstract fun recipeSearchCacheDao(): RecipeSearchCacheDao

    abstract fun shoppingListDao(): ShoppingListDao
}
