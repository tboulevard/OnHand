package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.AddToPantryUseCase
import com.tstreet.onhand.core.domain.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.RemoveFromPantryUseCase
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface IngredientSearchModule {

    @Binds
    // TODO: for some reason this scope isn't needed to bind use case to view lifecycle...is it
    // TODO: because [IngredientSearchComponent] already specifies this? Either way, keeping
    // TODO: here to be pedantic...
    @Singleton
    fun bindsGetIngredientsUseCase(
        useCase: GetIngredientsUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsAddToPantryUseCase(
        useCase: AddToPantryUseCase
    ): UseCase

    @Binds
    @Singleton
    fun bindsRemoveFromPantryUseCase(
        useCase: RemoveFromPantryUseCase
    ): UseCase
}