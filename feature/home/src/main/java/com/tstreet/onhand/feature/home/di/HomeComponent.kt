package com.tstreet.onhand.feature.home.di

import com.tstreet.onhand.core.common.CommonComponent
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.data.di.DataComponent
import com.tstreet.onhand.core.domain.di.UseCaseModule
import com.tstreet.onhand.core.domain.usecase.ingredientsearch.IngredientSearchUseCase
import com.tstreet.onhand.feature.home.HomeViewModel
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
interface HomeComponent {
    val viewModel: HomeViewModel
    val ingredientSearchUseCase: IngredientSearchUseCase
}