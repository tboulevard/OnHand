package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.core.common.CommonComponentProvider
import com.tstreet.onhand.core.data.di.DataComponentProvider
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchViewModel
import dagger.Component

@Component(
    dependencies = [
        DataComponentProvider::class,
        CommonComponentProvider::class
    ],
    modules = [IngredientSearchModule::class]
)
@IngredientSearchScope
interface IngredientSearchComponent {

    val viewModel: IngredientSearchViewModel
}