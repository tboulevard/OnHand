package com.tstreet.onhand.feature.customrecipe.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.customrecipe.AddRecipeUseCase
import dagger.Binds
import dagger.Module

@Module
interface CustomRecipeModule {

    @Binds
    fun bindsAddCustomRecipeUseCase(
        useCase: AddRecipeUseCase
    ): UseCase
}
