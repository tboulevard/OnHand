package com.tstreet.onhand.feature.recipesearch.di

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.GetRecipesUseCase
import dagger.Binds
import dagger.Module

@Module
interface RecipeSearchModule {

    @Binds
    // TODO: for some reason this scope isn't needed to bind use case to view lifecycle...is it
    // TODO: because [RecipeResultComponent] already specifies this? Either way, keeping
    // TODO: here to be pedantic...
    @FeatureScope
    fun GetRecipesUseCase.binds(): UseCase
}
