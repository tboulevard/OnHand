package com.tstreet.onhand.core.data.di

import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.repository.OnlineFirstIngredientSearchRepository
import com.tstreet.onhand.core.data.repository.OnlineFirstRecipeSearchRepository
import com.tstreet.onhand.core.data.repository.RecipeSearchRepository
import com.tstreet.onhand.core.network.di.NetworkModule
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule::class
    ]
)
interface DataModule {

    @Binds
    fun bindsIngredientSearchRepository(
        ingredientSearchRepository: OnlineFirstIngredientSearchRepository
    ): IngredientSearchRepository

    @Binds
    fun bindsRecipeSearchRepository(
        recipeSearchRepository: OnlineFirstRecipeSearchRepository
    ): RecipeSearchRepository
}