package com.tstreet.onhand.feature.recipesearch.di

import com.tstreet.onhand.core.data.di.DataComponentProvider
import com.tstreet.onhand.feature.recipesearch.RecipeSearchViewModel
import dagger.Component

@Component(
    dependencies = [DataComponentProvider::class],
    modules = [RecipeSearchModule::class]
)
@RecipeSearchScope
interface RecipeSearchComponent {

    val viewModel: RecipeSearchViewModel
}