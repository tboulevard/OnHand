package com.tstreet.onhand.feature.reciperesult.di

import com.tstreet.onhand.feature.reciperesult.RecipeResultViewModel
import dagger.Component

@Component(
    modules = [RecipeResultModule::class]
)
@RecipeResultScope
interface RecipeResultComponent {

    @Component.Builder
    interface Builder {
        fun build(): RecipeResultComponent
    }

    fun getViewModel(): RecipeResultViewModel
}