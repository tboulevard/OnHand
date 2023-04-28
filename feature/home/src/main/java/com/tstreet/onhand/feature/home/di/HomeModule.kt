package com.tstreet.onhand.feature.home.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.ingredients.GetIngredientsUseCase
import com.tstreet.onhand.core.domain.pantry.AddToPantryUseCase
import com.tstreet.onhand.core.domain.pantry.RemoveFromPantryUseCase
import dagger.Binds
import dagger.Module

@Module
interface HomeModule {

    @Binds
    fun bindsGetIngredientsUseCase(
        useCase: GetIngredientsUseCase
    ): UseCase

    @Binds
    fun bindsAddToPantryUseCase(
        useCase: AddToPantryUseCase
    ): UseCase

    @Binds
    fun bindsRemoveFromPantryUseCase(
        useCase: RemoveFromPantryUseCase
    ): UseCase
}
