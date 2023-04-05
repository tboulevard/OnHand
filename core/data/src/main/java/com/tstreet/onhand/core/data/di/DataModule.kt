package com.tstreet.onhand.core.data.di

import com.tstreet.onhand.core.data.repository.*
import com.tstreet.onhand.core.database.DaosModule
import com.tstreet.onhand.core.database.DatabaseModule
import com.tstreet.onhand.core.network.di.NetworkModule
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule::class,
        DatabaseModule::class,
        DaosModule::class
    ]
)
interface DataModule {

    @Binds
    // TODO: scope annotations at the module level seem to do nothing and are only
    //  needed at the class level? Look into later
    @Singleton
    fun bindsIngredientSearchRepository(
        ingredientSearchRepository: OfflineIngredientSearchRepository
    ): IngredientSearchRepository

    @Binds
    // TODO: scope annotations at the module level seem to do nothing and are only
    //  needed at the class level? Look into later
    @Singleton
    fun bindsRecipeSearchRepository(
        recipeSearchRepository: RecipeRepositoryImpl
    ): RecipeRepository

    @Binds
    // TODO: scope annotations at the module level seem to do nothing and are only
    //  needed at the class level? Look into later
    @Singleton
    fun bindsPantryRepository(
        pantryRepository: OfflinePantryRepository
    ): PantryRepository
}
