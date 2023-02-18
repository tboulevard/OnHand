package com.tstreet.onhand.feature.reciperesult.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.feature.reciperesult.GetRecipesUseCase
import dagger.Binds
import dagger.Module

@Module
interface RecipeResultModule {

    @Binds
    // TODO: for some reason this scope isn't needed to bind use case to view lifecycle...is it
    // TODO: because [RecipeResultComponent] already specifies this? Either way, keeping
    // TODO: here to be pedantic...
    @RecipeResultScope
    fun GetRecipesUseCase.binds(): UseCase
}