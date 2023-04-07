package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.database.dao.RecipeSearchCacheDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.model.RecipeSearchCacheEntity
import com.tstreet.onhand.core.database.model.asExternalModel
import com.tstreet.onhand.core.database.model.toEntity
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class RecipeRepositoryImpl @Inject constructor(
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>,
    private val savedRecipeDao: Provider<SavedRecipeDao>,
    private val recipeSearchCacheDao: Provider<RecipeSearchCacheDao>,
) : RecipeRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override suspend fun findRecipes(
        fetchStrategy: FetchStrategy,
        ingredients: List<String>
    ): List<Recipe> {
        println("[OnHand] findRecipes($fetchStrategy, $ingredients)")

        // TODO: this would be much cleaner with a Flow<>, look into before merging PR
        return when (fetchStrategy) {
            FetchStrategy.DATABASE -> {
                recipeSearchCacheDao
                    .get()
                    .getRecipeSearchResult()
                    .map(RecipeSearchCacheEntity::asExternalModel)
            }
            FetchStrategy.NETWORK -> {
                val result: List<Recipe> = onHandNetworkDataSource
                    .get()
                    .findRecipesFromIngredients(ingredients)
                    .map(NetworkRecipe::asExternalModel)

                // TODO: this is getting kind of business logic-y too...refactor
                //  Potentially expose each of these actions as a method and allow use case to call?
                recipeSearchCacheDao.get().clear()

                recipeSearchCacheDao
                    .get()
                    .addRecipeSearchResult(
                        result.map(Recipe::toEntity)
                    )

                result
            }
        }
    }

    override fun getRecipeDetail(id: Int): Flow<RecipeDetail> {
        println("[OnHand] getRecipeDetail($id)")
        return onHandNetworkDataSource
            .get()
            .getRecipeDetail(id)
            .map(NetworkRecipeDetail::asExternalModel)
    }

    override suspend fun saveRecipe(recipeDetail: RecipeDetail) {
        println("[OnHand] saveRecipe($recipeDetail)")
        savedRecipeDao
            .get()
            .addRecipe(recipeDetail.toEntity())
    }

    override suspend fun unsaveRecipe(id: Int) {
        println("[OnHand] unsaveRecipe($id)")
        savedRecipeDao
            .get()
            .deleteRecipe(id)
    }

    override suspend fun isRecipeSaved(id: Int): Boolean {
        println("[OnHand] isRecipeSaved($id)")
        return savedRecipeDao
            .get()
            .isRecipeSaved(id) == 1
    }
}
