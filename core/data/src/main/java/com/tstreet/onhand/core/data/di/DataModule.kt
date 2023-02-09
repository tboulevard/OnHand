package com.tstreet.onhand.core.data.di

import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.repository.OnlineFirstIngredientSearchRepository
import com.tstreet.onhand.core.data.repository.OnlineFirstRecipeSearchRepository
import com.tstreet.onhand.core.data.repository.RecipeSearchRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DataModule {

    @Binds
    @Singleton
    fun bindsIngredientSearchRepository(
        ingredientSearchRepository: OnlineFirstIngredientSearchRepository
    ): IngredientSearchRepository

    @Binds
    @Singleton
    fun bindsRecipeSearchRepository(
        recipeSearchRepository: OnlineFirstRecipeSearchRepository
    ): RecipeSearchRepository
}