package com.tstreet.onhand.feature.recipedetail.di

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.GetRecipeDetailUseCase
import dagger.Binds
import dagger.Module

@Module
interface RecipeDetailModule {

    @Binds
    @FeatureScope
    fun GetRecipeDetailUseCase.binds(): UseCase
}
