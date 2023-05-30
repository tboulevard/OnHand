package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.common.FetchStrategy.*
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.data.api.repository.RecipeRepository
import com.tstreet.onhand.core.database.dao.RecipeSearchCacheDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.model.*
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import com.tstreet.onhand.core.network.model.NetworkRecipeIngredient
import com.tstreet.onhand.core.network.retrofit.NetworkResponse.*
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
    ): Resource<List<Recipe>> {
        println("[OnHand] findRecipes($fetchStrategy, $ingredients)")

        return when (fetchStrategy) {
            DATABASE -> {
                Resource.success(data = getCachedRecipeSearchResults())
            }
            NETWORK -> {
                val networkResponse =
                    onHandNetworkDataSource
                        .get()
                        .findRecipesFromIngredients(ingredients)

                return when (networkResponse) {
                    is Success -> {
                        val externalModel = networkResponse.body.map(NetworkRecipe::asExternalModel)
                        cacheRecipeSearchResults(externalModel)
                        Resource.success(data = externalModel)
                    }
                    is ApiError,
                    is NetworkError,
                    is UnknownError -> {
                        Resource.error(
                            msg = "${networkResponse::class.java.simpleName}, please check your " +
                                    "device's network connectivity.\n\nShowing last calculated " +
                                    "search result.",
                            data = getCachedRecipeSearchResults()
                        )
                    }
                }
            }
        }
    }

    override suspend fun getRecipeDetail(id: Int): Resource<RecipeDetail> {
        println("[OnHand] getRecipeDetail($id)")
        val networkResponse = onHandNetworkDataSource
            .get()
            .getRecipeDetail(id)

        return when (networkResponse) {
            is Success -> {
                Resource.success(
                    data = networkResponse.body.asExternalModel()
                )
            }
            is ApiError,
            is NetworkError,
            is UnknownError -> {
                Resource.error(
                    msg = "${networkResponse::class.java.simpleName}, please check your " +
                            "device's network connectivity. Unable to view recipe.",
                )
            }
        }
    }

    override suspend fun saveRecipe(
        recipe: Recipe
    ) {
        println("[OnHand] saveRecipe($recipe)")
        savedRecipeDao
            .get()
            .addRecipe(recipe.toSavedRecipeEntity())
    }

    override suspend fun saveCustomRecipe(
        recipe : Recipe,
        detail: RecipeDetail
    ) {
        println("[OnHand] saveCustomRecipe($recipe)")
        savedRecipeDao
            .get()
            .addRecipe(createCustomSavedRecipeEntity(recipe, detail))
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
            .map { it.map(SavedRecipeEntity::asSaveableRecipe) }
    }

    override suspend fun updateSavedRecipesMissingIngredient(ingredient: Ingredient) {
        println("[OnHand] updateSavedRecipesMissingIngredient($ingredient)")
        try {
            savedRecipeDao
                .get()
                .updateRecipesMissingIngredient(ingredient.name)
        } catch (e: Exception) {
            // TODO: log analytics here
            // TODO: rethrow in debug
        }
    }

    override suspend fun updateSavedRecipesUsingIngredient(ingredient: Ingredient) {
        println("[OnHand] updateSavedRecipesUsingIngredient($ingredient)")
        try {
            savedRecipeDao
                .get()
                .updateRecipesUsingIngredient(ingredient.name)
        } catch (e: Exception) {
            // TODO: log analytics here
            // TODO: rethrow in debug
        }
    }

    override suspend fun getCustomRecipeDetail(id: Int): Resource<RecipeDetail> {
        println("[OnHand] getCustomRecipeDetail($id)")

        return try {
            Resource.success(
                data = savedRecipeDao
                    .get()
                    .getRecipe(id)
                    .asCustomRecipeDetail()
            )
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    private suspend fun getCachedRecipeSearchResults(): List<Recipe> {
        return recipeSearchCacheDao
            .get()
            .getRecipeSearchResult()
            .map(RecipeSearchCacheEntity::asSaveableRecipe)
    }

    private suspend fun cacheRecipeSearchResults(recipes: List<Recipe>) {
        recipeSearchCacheDao
            .get()
            .cacheRecipeSearchResult(
                recipes.map(Recipe::toSearchCacheEntity)
            )
    }
}

// TODO: potentially move to more appropriate spot...
private fun NetworkRecipeDetail.asExternalModel() = RecipeDetail(
    instructions = instructions ?: "No instructions provided." // TODO: revisit
)

private fun NetworkRecipe.asExternalModel() = Recipe(
    id = id,
    title = title,
    image = image,
    imageType = imageType,
    usedIngredientCount = usedIngredientCount,
    usedIngredients = usedIngredients.map { it.asExternalModel() },
    missedIngredientCount = missedIngredientCount,
    missedIngredients = missedIngredients.map { it.asExternalModel() },
    likes = likes,
    isCustom = false
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
