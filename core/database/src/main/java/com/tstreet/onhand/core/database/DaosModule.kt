package com.tstreet.onhand.core.database

import android.util.Log
import com.tstreet.onhand.core.database.dao.IngredientDao
import com.tstreet.onhand.core.database.dao.PantryDao
import com.tstreet.onhand.core.database.dao.RecipeSearchCacheDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.dao.ShoppingListDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DaosModule {

    @Provides
    fun providesIngredientDao(
        database: OnHandDatabase,
    ): IngredientDao = database.ingredientDao()
        .also { Log.d("[OnHand]", "IngredientDao created") }

    @Provides
    fun providesSavedRecipeDao(
        database: OnHandDatabase,
    ): SavedRecipeDao = database.savedRecipeDao()
        .also { Log.d("[OnHand]", "SavedRecipeDao created") }

    @Provides
    fun providesRecipeSearchCacheDao(
        database: OnHandDatabase,
    ): RecipeSearchCacheDao = database.recipeSearchCacheDao()
        .also { Log.d("[OnHand]", "RecipeSearchCacheDao created") }

    @Provides
    fun providesShoppingListDao(
        database: OnHandDatabase,
    ): ShoppingListDao = database.shoppingListDao()
        .also { Log.d("[OnHand]", "ShoppingListDao created") }

    @Provides
    fun providesPantryDao(
        database: OnHandDatabase,
    ): PantryDao = database.pantryDao()
        .also { Log.d("[OnHand]", "PantryDao created") }
}
