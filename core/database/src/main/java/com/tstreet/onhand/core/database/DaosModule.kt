package com.tstreet.onhand.core.database

import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import dagger.Module
import dagger.Provides

@Module
object DaosModule {

    @Provides
    fun providesIngredientDao(
        database: OnHandDatabase,
    ): IngredientCatalogDao = database.ingredientDao()
        .also { println("[OnHand] IngredientCatalogDao created") }

    @Provides
    fun providesSavedRecipeDao(
        database: OnHandDatabase,
    ): SavedRecipeDao = database.savedRecipeDao()
        .also { println("[OnHand] SavedRecipeDao created") }
}
