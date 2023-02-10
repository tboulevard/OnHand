package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.core.data.di.DataComponent
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchViewModel
import dagger.Component

@Component(
    dependencies = [DataComponent::class],
    modules = [IngredientSearchModule::class]
)
@IngredientSearchScope
interface IngredientSearchComponent {

    val viewModel: IngredientSearchViewModel
}