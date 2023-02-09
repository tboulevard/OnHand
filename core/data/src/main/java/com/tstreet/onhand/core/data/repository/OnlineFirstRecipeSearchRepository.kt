package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject

// TODO: refactor - just here for dagger testing for now...
class OnlineFirstRecipeSearchRepository @Inject constructor(
    private val onHandNetworkDataSource: OnHandNetworkDataSource
) : RecipeSearchRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override fun searchRecipes(prefix: String): List<NetworkIngredient> {
        return onHandNetworkDataSource.getIngredients(prefix)
    }
}