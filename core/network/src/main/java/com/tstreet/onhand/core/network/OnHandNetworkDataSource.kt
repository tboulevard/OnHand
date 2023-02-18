package com.tstreet.onhand.core.network

import com.tstreet.onhand.core.network.model.NetworkIngredient

/**
 * Interface representing network calls to the OnHand backend
 */
interface OnHandNetworkDataSource {
    fun getIngredients(prefix: String): List<NetworkIngredient>
}
