package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.network.model.NetworkIngredient

// TODO: refactor - just here for dagger testing for now...
interface RecipeSearchRepository {
    fun searchRecipes(prefix: String): List<NetworkIngredient>
}