package com.tstreet.onhand.core.network

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.network.model.NetworkIngredient
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import com.tstreet.onhand.core.network.retrofit.GenericError
import com.tstreet.onhand.core.network.retrofit.NetworkResponse
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing network calls to the OnHand backend
 */
interface OnHandNetworkDataSource {
    fun getIngredients(prefix: String): List<NetworkIngredient>

    suspend fun findRecipesFromIngredients(ingredients: List<String>): NetworkResponse<List<NetworkRecipe>, GenericError>

    fun getRecipeDetail(id: Int): Flow<NetworkRecipeDetail>
}
