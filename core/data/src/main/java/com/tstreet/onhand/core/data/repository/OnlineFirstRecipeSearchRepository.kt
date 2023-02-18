package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject
import javax.inject.Provider

// TODO: refactor - just here for dagger testing for now...
class OnlineFirstRecipeSearchRepository @Inject constructor(
    // Provider<> makes dependency creation lazy (only created when needed), allowing
    // us to defer class creation until we actually use the network
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>
) : RecipeSearchRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override fun searchRecipes(prefix: String): List<NetworkIngredient> {
        return onHandNetworkDataSource.get().getIngredients(prefix)
    }
}