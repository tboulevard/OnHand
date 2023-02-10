package com.tstreet.onhand.feature.reciperesult.di

import com.tstreet.onhand.core.data.repository.RecipeSearchRepository
import com.tstreet.onhand.feature.reciperesult.GetRecipesUseCase
import dagger.Module
import dagger.Provides

@Module
class RecipeResultModule {

    @Provides
    fun provideGetRecipes(repo : RecipeSearchRepository) : GetRecipesUseCase {
        return GetRecipesUseCase(repo)
    }
}