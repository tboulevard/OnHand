package com.tstreet.onhand.feature.reciperesult.di

import com.tstreet.onhand.core.data.di.DataModule
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.network.di.NetworkModule
import com.tstreet.onhand.feature.reciperesult.RecipeResultViewModel
import dagger.Module
import dagger.Provides

@Module(
    includes = [
        DataModule::class,
        NetworkModule::class
    ]
)
class RecipeResultModule {

    @Provides
    @RecipeResultScope
    fun provideViewModel(useCase : GetIngredientsUseCase) : RecipeResultViewModel {
        return RecipeResultViewModel(useCase)
    }
}