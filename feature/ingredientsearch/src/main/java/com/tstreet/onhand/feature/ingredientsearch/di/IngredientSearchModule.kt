package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.feature.ingredientsearch.GetIngredientsUseCase
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchViewModel
import dagger.Module
import dagger.Provides

@Module
class IngredientSearchModule {

    @Provides
    fun provideGetIngredients(repo : IngredientSearchRepository) : GetIngredientsUseCase {
        return GetIngredientsUseCase(repo)
    }
}