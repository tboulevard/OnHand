package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkIngredient
import javax.inject.Inject

class OnlineFirstIngredientSearchRepository @Inject constructor(
    private val onHandNetworkDataSource: OnHandNetworkDataSource
) : IngredientSearchRepository {
    // TODO: super messy...refactor later
    override fun searchIngredients(prefix: String): List<Ingredient> {
        val networkIngredientList = onHandNetworkDataSource.getIngredients(prefix)
        val externalIngredientList = mutableListOf<Ingredient>()
        networkIngredientList.forEach {
            externalIngredientList.add(it.toExternalIngredientModel())
        }
        return externalIngredientList
    }

    // TODO: move to domain layer (or whereever appropriate to bridge internal -> external model
    // TODO: representation)
    private fun NetworkIngredient.toExternalIngredientModel(): Ingredient {
        return Ingredient(
            name = this.name,
            image = this.image,
            childIngredient = this.children
        )
    }
}
