package com.tstreet.onhand.core.network

import com.tstreet.onhand.core.network.model.NetworkIngredient
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing network calls to the OnHand backend
 */
interface OnHandNetworkDataSource {
    fun getIngredients(prefix: String): List<NetworkIngredient>

    fun getRecipesFromIngredients(ingredients : List<String>) : Flow<List<NetworkRecipe>>

    fun getRecipeDetail(id: Int) : Flow<NetworkRecipeDetail>
}
