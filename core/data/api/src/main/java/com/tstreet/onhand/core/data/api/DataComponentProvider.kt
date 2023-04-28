package com.tstreet.onhand.core.data.api

import androidx.compose.runtime.compositionLocalOf
import com.tstreet.onhand.core.data.api.repository.IngredientSearchRepository
import com.tstreet.onhand.core.data.api.repository.PantryRepository
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository

interface DataComponentProvider {

    val ingredientSearchRepository: IngredientSearchRepository
    val recipeRepository: RecipeRepository
    val pantryRepository: PantryRepository
    val shoppingListRepository: ShoppingListRepository
}

val LocalDataProvider = compositionLocalOf<DataComponentProvider> {
    error("DataComponentProvider not found.")
}