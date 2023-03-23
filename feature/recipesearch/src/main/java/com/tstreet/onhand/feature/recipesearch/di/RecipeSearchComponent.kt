package com.tstreet.onhand.feature.recipesearch.di

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.di.DataComponentProvider
import com.tstreet.onhand.feature.recipesearch.RecipeSearchViewModel
import dagger.Component

@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ],
    modules = [RecipeSearchModule::class]
)
@FeatureScope
interface RecipeSearchComponent {

    val viewModel: RecipeSearchViewModel
}
