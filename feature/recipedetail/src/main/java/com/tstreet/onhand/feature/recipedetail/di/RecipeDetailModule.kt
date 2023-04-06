package com.tstreet.onhand.feature.recipedetail.di

import com.tstreet.onhand.core.common.UseCase
import com.tstreet.onhand.core.domain.GetRecipeDetailUseCase
import dagger.Binds
import dagger.Module

@Module
interface RecipeDetailModule {

    // NOTE: We recreate an instance of this class each time we navigate. Probably due to
    //  the @FeatureScope on the class binding it to the RecipeSearch screen scope. Make
    //    this singleton?
    @Binds
    fun GetRecipeDetailUseCase.binds(): UseCase
}
