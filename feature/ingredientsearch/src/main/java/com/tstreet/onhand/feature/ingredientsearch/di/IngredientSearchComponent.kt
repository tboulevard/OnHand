package com.tstreet.onhand.feature.ingredientsearch.di

import com.tstreet.onhand.MainActivity
import com.tstreet.onhand.OnHandApplicationComponent
import com.tstreet.onhand.feature.ingredientsearch.IngredientSearchViewModel
import dagger.Component

@Component(
    dependencies = [OnHandApplicationComponent::class]
)
@IngredientSearchScope
interface IngredientSearchComponent {

    @Component.Factory
    interface Factory {
        // Takes an instance of AppComponent when creating
        // an instance of IngredientSearchComponent
        fun create(appComponent: OnHandApplicationComponent): IngredientSearchComponent
    }

    fun getViewModel(): IngredientSearchViewModel

    fun inject(activity : MainActivity)
}