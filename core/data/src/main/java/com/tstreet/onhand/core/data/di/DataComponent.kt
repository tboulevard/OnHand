package com.tstreet.onhand.core.data.di

import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.repository.RecipeSearchRepository
import dagger.Component

@Component(
    modules = [
        DataModule::class
    ]
)
interface DataComponent {

    // TODO: play around with not even having this component and just using the modules directly
    // (to avoid needing these methods and this class?)
    fun ingredientSearchRepository() : IngredientSearchRepository
    fun recipeSearchRepository() : RecipeSearchRepository
}