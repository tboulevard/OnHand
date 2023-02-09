package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchViewModel
import dagger.Component

@Component(
    modules = [IngredientSearchModule::class]
)
@IngredientSearchScope
interface IngredientSearchComponent {

    @Component.Builder
    interface Builder {
        fun build(): IngredientSearchComponent
    }

    fun getViewModel(): IngredientSearchViewModel
}