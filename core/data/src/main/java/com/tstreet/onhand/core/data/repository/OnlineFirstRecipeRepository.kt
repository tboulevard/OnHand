package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class OnlineFirstRecipeRepository @Inject constructor(
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>
) : RecipeRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override suspend fun searchRecipes(ingredients: List<String>): List<Recipe> {
        println("[OnHand] OnlineFirstRecipeSearchRepository.searchRecipes()")

        return onHandNetworkDataSource
            .get()
            .getRecipesFromIngredients(ingredients)
            .map(NetworkRecipe::asExternalModel)
    }

    override fun getRecipeDetail(id: Int): Flow<RecipeDetail> {
        return onHandNetworkDataSource
            .get()
            .getRecipeDetail(id)
            .map(NetworkRecipeDetail::asExternalModel)
    }

    override fun saveRecipe(recipeDetail: RecipeDetail): Flow<Boolean> {
        return flow { emit(true) }
    }
}
