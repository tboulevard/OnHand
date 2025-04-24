package com.tstreet.onhand.feature.customrecipe.di

import com.tstreet.onhand.core.common.CommonComponent
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.impl.di.DataComponent
import com.tstreet.onhand.feature.customrecipe.CreateCustomRecipeViewModel
import dagger.Component

@Component(
    dependencies = [
        DataComponent::class,
        CommonComponent::class
    ],
    modules = [
        CustomRecipeModule::class
    ]
)
@FeatureScope
interface CustomRecipeComponent {

    val viewModel: CreateCustomRecipeViewModel
}
