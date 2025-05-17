package com.tstreet.onhand.feature.customrecipe.di

import com.tstreet.onhand.core.domain.usecase.UseCase
import com.tstreet.onhand.core.domain.usecase.customrecipe.AddRecipeUseCase
import com.tstreet.onhand.core.domain.usecase.customrecipe.CustomRecipeInputUseCase
import dagger.Binds
import dagger.Module

@Module
interface CustomRecipeModule {

    @Binds
    fun bindsAddCustomRecipeUseCase(
        useCase: AddRecipeUseCase
    ): UseCase

    @Binds
    fun bindsValidateCustomRecipeInputUseCase(
        useCase: CustomRecipeInputUseCase
    ): UseCase
}
