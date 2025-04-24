package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.core.common.CommonComponent
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.impl.di.DataComponent
import com.tstreet.onhand.core.domain.di.UseCaseModule
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchViewModel
import dagger.Component

@Component(
    dependencies = [
        DataComponent::class,
        CommonComponent::class
    ],
    modules = [
        UseCaseModule::class
    ]
)
@FeatureScope
interface IngredientSearchComponent {

    val viewModel: IngredientSearchViewModel
}
