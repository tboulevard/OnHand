package com.tstreet.onhand.core.database

import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.dao.RecipeSearchCacheDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.dao.ShoppingListDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DaosModule {

    @Provides
    // TODO: having scope annotation here works - this is different than in DataModule
    //  There we pleace scope annotations at the class level. Look into why later...
    @Singleton
    fun providesIngredientDao(
        database: OnHandDatabase,
    ): IngredientCatalogDao = database.ingredientDao()
        .also { println("[OnHand] IngredientCatalogDao created") }

    @Provides
    @Singleton
    fun providesSavedRecipeDao(
        database: OnHandDatabase,
    ): SavedRecipeDao = database.savedRecipeDao()
        .also { println("[OnHand] SavedRecipeDao created") }

    @Provides
    @Singleton
    fun providesRecipeSearchCacheDao(
        database: OnHandDatabase,
    ): RecipeSearchCacheDao = database.recipeSearchCacheDao()
        .also { println("[OnHand] RecipeSearchCacheDao created") }

    @Provides
    @Singleton
    fun providesShoppingListDao(
        database: OnHandDatabase,
    ): ShoppingListDao = database.shoppingListDao()
        .also { println("[OnHand] ShoppingListDao created") }
}
