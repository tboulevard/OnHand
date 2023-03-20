package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkRecipe
import javax.inject.Inject
import javax.inject.Provider

class OnlineFirstRecipeSearchRepository @Inject constructor(
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>
) : RecipeSearchRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override suspend fun searchRecipes(ingredients: List<String>): List<Recipe> {
        return onHandNetworkDataSource
            .get()
            .getRecipesFromIngredients(ingredients)
            .map(NetworkRecipe::asExternalModel)
    }
}