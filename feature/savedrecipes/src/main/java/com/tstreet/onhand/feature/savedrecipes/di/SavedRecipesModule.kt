package com.tstreet.onhand.feature.savedrecipes.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.GetSavedRecipesUseCase
import dagger.Binds
import dagger.Module

@Module
interface SavedRecipesModule {

    @Binds
    fun bindsGetRecipesUseCase(
        useCase: GetSavedRecipesUseCase
    ): UseCase
}
