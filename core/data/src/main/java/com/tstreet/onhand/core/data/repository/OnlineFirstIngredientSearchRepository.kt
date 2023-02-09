package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject

class OnlineFirstIngredientSearchRepository @Inject constructor(
    private val onHandNetworkDataSource: OnHandNetworkDataSource
) : IngredientSearchRepository {
    // TODO: Refactor the fact that we're exposing NetworkIngredient to Domain layer
    override fun searchIngredients(prefix: String): List<NetworkIngredient> {
        return onHandNetworkDataSource.getIngredients(prefix)
    }
}
