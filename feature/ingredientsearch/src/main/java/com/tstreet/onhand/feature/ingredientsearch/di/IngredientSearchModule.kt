package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.core.data.di.DataModule
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.network.di.NetworkModule
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchViewModel
import dagger.Module
import dagger.Provides

@Module(
    includes = [
        DataModule::class,
        NetworkModule::class
    ]
)
class IngredientSearchModule {

    @Provides
    @IngredientSearchScope
    fun provideViewModel(useCase : GetIngredientsUseCase) : IngredientSearchViewModel {
        return IngredientSearchViewModel(useCase)
    }
}