package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class OnlineFirstRecipeSearchRepository @Inject constructor(
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>
) : RecipeSearchRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override fun searchRecipes(ingredients: List<String>): Flow<List<Recipe>> {
        println("[OnHand] OnlineFirstRecipeSearchRepository.searchRecipes()")

        return onHandNetworkDataSource
            .get()
            .getRecipesFromIngredients(ingredients)
            .map{ it.map(NetworkRecipe::asExternalModel) }
    }
}