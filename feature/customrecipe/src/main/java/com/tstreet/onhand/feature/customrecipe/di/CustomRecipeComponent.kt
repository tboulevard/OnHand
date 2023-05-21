package com.tstreet.onhand.feature.customrecipe.di

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.api.di.DataComponentProvider
import com.tstreet.onhand.feature.customrecipe.AddCustomRecipeViewModel
import dagger.Component

@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ],
    modules = [CustomRecipeModule::class]
)
@FeatureScope
interface CustomRecipeComponent {

    val viewModel: AddCustomRecipeViewModel
}