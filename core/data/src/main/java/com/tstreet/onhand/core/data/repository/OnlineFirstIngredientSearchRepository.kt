package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject
import javax.inject.Provider

// TODO: refactor to offline first...
class OnlineFirstIngredientSearchRepository @Inject constructor(
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>
) : IngredientSearchRepository {
    // TODO: Refactor the fact that we're exposing NetworkIngredient to Domain layer

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }
    override fun searchIngredients(prefix: String): List<NetworkIngredient> {
        return onHandNetworkDataSource.get().getIngredients(prefix)
    }
}
