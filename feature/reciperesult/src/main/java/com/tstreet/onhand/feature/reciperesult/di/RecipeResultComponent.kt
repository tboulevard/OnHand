package com.tstreet.onhand.feature.reciperesult.di

import com.tstreet.onhand.core.data.di.DataComponentProvider
import com.tstreet.onhand.feature.reciperesult.RecipeResultViewModel
import dagger.Component

@Component(
    dependencies = [DataComponentProvider::class],
    modules = [RecipeResultModule::class]
)
@RecipeResultScope
interface RecipeResultComponent {

    val viewModel: RecipeResultViewModel
}