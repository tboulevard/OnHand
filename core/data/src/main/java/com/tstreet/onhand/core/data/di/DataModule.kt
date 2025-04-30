package com.tstreet.onhand.core.data.di

import com.tstreet.onhand.core.domain.repository.IngredientSearchRepository
import com.tstreet.onhand.core.domain.repository.PantryRepository
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.data.repository.OfflineIngredientSearchRepositoryImpl
import com.tstreet.onhand.core.data.repository.OfflinePantryRepositoryImpl
import com.tstreet.onhand.core.data.repository.RecipeRepositoryImpl
import com.tstreet.onhand.core.data.repository.ShoppingListRepositoryImpl
import com.tstreet.onhand.core.database.DaosModule
import com.tstreet.onhand.core.database.DatabaseModule
import com.tstreet.onhand.core.network.di.NetworkModule
import dagger.Binds
import dagger.Module

@Module(
    includes = [
        NetworkModule::class,
        DatabaseModule::class,
        DaosModule::class
    ]
)
interface DataModule {

    @Binds
    fun bindsIngredientSearchRepository(
        ingredientSearchRepository: OfflineIngredientSearchRepositoryImpl
    ): IngredientSearchRepository

    @Binds
    fun bindsRecipeSearchRepository(
        recipeSearchRepository: RecipeRepositoryImpl
    ): RecipeRepository

    @Binds
    fun bindsPantryRepository(
        pantryRepository: OfflinePantryRepositoryImpl
    ): PantryRepository

    @Binds
    fun bindsShoppingListRepository(
        shoppingListRepository: ShoppingListRepositoryImpl
    ): ShoppingListRepository
}
