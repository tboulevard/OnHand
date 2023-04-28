package com.tstreet.onhand.feature.savedrecipes.di

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.api.DataComponentProvider
import com.tstreet.onhand.feature.savedrecipes.SavedRecipesViewModel
import dagger.Component

@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ],
    modules = [SavedRecipesModule::class]
)
@FeatureScope
interface SavedRecipesComponent {

    val viewModel : SavedRecipesViewModel
}
