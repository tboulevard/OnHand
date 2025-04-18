package com.tstreet.onhand.core.data.api.di

import android.util.Log
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
    Log.d("[OnHand]", "LocalDataProvider not found.")
    error("DataComponentProvider not found.")
}