package com.tstreet.onhand.core.data.di

import androidx.compose.runtime.compositionLocalOf
import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.repository.RecipeSearchRepository

// TODO: move to api module...
interface DataComponentProvider {

    val ingredientSearchRepository : IngredientSearchRepository
    val recipeSearchRepository : RecipeSearchRepository
}

val LocalDataProvider = compositionLocalOf<DataComponentProvider> {
    error("DataComponentProvider not found.")
}