package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.database.dao.RecipeSearchCacheDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.model.*
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import com.tstreet.onhand.core.network.model.NetworkRecipeIngredient
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

        // TODO: this might be cleaner if we deal with just Flows here
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
                        result.map(Recipe::toSearchCacheEntity)
                    )

                result
            }
        }
    }

    override fun getRecipeDetail(id: Int): Flow<RecipeDetail> {
        println("[OnHand] getRecipeDetail($id) - from Network")
        return onHandNetworkDataSource
            .get()
            .getRecipeDetail(id)
            .map(NetworkRecipeDetail::asExternalModel)
    }

    override suspend fun saveRecipe(recipe: Recipe) {
        println("[OnHand] saveRecipe($recipe)")
        savedRecipeDao
            .get()
            .addRecipe(recipe.toSavedRecipeEntity())
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

    override fun getSavedRecipes(): Flow<List<SaveableRecipe>> {
        println("[OnHand] getSavedRecipes()")
        return savedRecipeDao
            .get()
            .getSavedRecipes()
            .map { it.map(SavedRecipeEntity::asExternalModel) }
    }
}

// TODO: potentially move to more appropriate spot...
private fun NetworkRecipe.asExternalModel() = Recipe(
    id = id,
    title = title,
    image = image,
    imageType = imageType,
    usedIngredientCount = usedIngredientCount,
    usedIngredients = usedIngredients.map { it.asExternalModel() },
    missedIngredientCount = missedIngredientCount,
    missedIngredients = missedIngredients.map { it.asExternalModel() },
    likes = likes
)

private fun NetworkRecipeIngredient.asExternalModel() = RecipeIngredient(
    Ingredient(
        id = id,
        name = name,
    ),
    image = image,
    amount = amount,
    unit = unit,
)

private fun NetworkRecipeDetail.asExternalModel() = RecipeDetail(
    id = id,
    // TODO: Determine whether it's best to just transmit an empty src url or some other state
    sourceUrl = sourceUrl ?: ""
)
