package com.tstreet.onhand.feature.recipesearch.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.GetRecipesUseCase
import com.tstreet.onhand.core.domain.SaveRecipeUseCase
import com.tstreet.onhand.core.domain.UnsaveRecipeUseCase
import dagger.Binds
import dagger.Module

@Module
interface RecipeSearchModule {

    @Binds
    fun bindsGetRecipesUseCase(
        useCase: GetRecipesUseCase
    ): UseCase

    @Binds
    fun bindsSaveRecipesUseCase(
        useCase: SaveRecipeUseCase
    ): UseCase

    @Binds
    fun bindsUnsaveRecipesUseCase(
        useCase: UnsaveRecipeUseCase
    ): UseCase
}
