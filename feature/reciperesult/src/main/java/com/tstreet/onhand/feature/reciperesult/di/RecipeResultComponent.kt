package com.tstreet.onhand.feature.reciperesult.di

import com.tstreet.onhand.MainActivity
import com.tstreet.onhand.OnHandApplicationComponent
import com.tstreet.onhand.feature.reciperesult.RecipeResultViewModel
import dagger.Component

@Component(
    dependencies = [OnHandApplicationComponent::class]
)
@RecipeResultScope
interface RecipeResultComponent {

    @Component.Factory
    interface Builder {
        fun create(appComponent: OnHandApplicationComponent): RecipeResultComponent
    }

    fun getViewModel(): RecipeResultViewModel

    fun inject(activity : MainActivity)
}