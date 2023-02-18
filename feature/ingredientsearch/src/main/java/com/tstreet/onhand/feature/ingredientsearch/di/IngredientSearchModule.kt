package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.feature.ingredientsearch.GetIngredientsUseCase
import dagger.Binds
import dagger.Module

@Module
interface IngredientSearchModule {

    @Binds
    // TODO: for some reason this scope isn't needed to bind use case to view lifecycle...is it
    // TODO: because [IngredientSearchComponent] already specifies this? Either way, keeping
    // TODO: here to be pedantic...
    @IngredientSearchScope
    fun GetIngredientsUseCase.binds() : UseCase
}