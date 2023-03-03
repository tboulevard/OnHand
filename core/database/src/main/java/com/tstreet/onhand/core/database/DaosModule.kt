package com.tstreet.onhand.core.database

import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import dagger.Module
import dagger.Provides

@Module
object DaosModule {

    @Provides
    fun providesIngredientDao(
        database: OnHandDatabase,
    ): IngredientCatalogDao = database.ingredientDao()
}