package com.tstreet.onhand.core.network

import com.tstreet.onhand.core.network.model.NetworkIngredient
import com.tstreet.onhand.core.network.model.NetworkRecipe

/**
 * Interface representing network calls to the OnHand backend
 */
interface OnHandNetworkDataSource {
    fun getIngredients(prefix: String): List<NetworkIngredient>

    fun getRecipesFromIngredients(ingredients : List<String>) : List<NetworkRecipe>
}
