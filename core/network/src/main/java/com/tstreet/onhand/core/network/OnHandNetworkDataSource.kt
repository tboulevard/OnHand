package com.tstreet.onhand.core.network

import com.tstreet.onhand.core.network.model.NetworkIngredient
import com.tstreet.onhand.core.network.model.NetworkIngredientSearchResult
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import com.tstreet.onhand.core.network.retrofit.OnHandNetworkResponse
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing network calls to the OnHand backend
 */
interface OnHandNetworkDataSource {
    suspend fun getIngredients(prefix: String): OnHandNetworkResponse<NetworkIngredientSearchResult>

    suspend fun findRecipesFromIngredients(
        ingredients: List<String>
    ): OnHandNetworkResponse<List<NetworkRecipe>>

    suspend fun getRecipeDetail(id: Int): OnHandNetworkResponse<NetworkRecipeDetail>
}
