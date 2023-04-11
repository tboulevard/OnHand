package com.tstreet.onhand.core.data.di

import androidx.compose.runtime.compositionLocalOf
import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.repository.PantryRepository
import com.tstreet.onhand.core.data.repository.RecipeRepository
import com.tstreet.onhand.core.data.repository.ShoppingListRepository

// TODO: move to api module...
interface DataComponentProvider {

    val ingredientSearchRepository: IngredientSearchRepository
    val recipeRepository: RecipeRepository
    val pantryRepository: PantryRepository
    val shoppingListRepository: ShoppingListRepository
}

val LocalDataProvider = compositionLocalOf<DataComponentProvider> {
    error("DataComponentProvider not found.")
}