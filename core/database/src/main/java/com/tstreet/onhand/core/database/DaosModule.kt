package com.tstreet.onhand.core.database

import com.tstreet.onhand.core.database.dao.IngredientCatalogDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DaosModule {

    @Provides
    fun providesIngredientDao(
        database: OnHandDatabase,
    ): IngredientCatalogDao = database.ingredientDao()
        .also { println("[OnHand] IngredientCatalogDao created") }
}
