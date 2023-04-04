package com.tstreet.onhand.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.model.IngredientCatalogEntity
import com.tstreet.onhand.core.database.model.SavedRecipeEntity

@Database(
    entities = [IngredientCatalogEntity::class, SavedRecipeEntity::class], version = 1
)
abstract class OnHandDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientCatalogDao

    abstract fun savedRecipeDao(): SavedRecipeDao
}
