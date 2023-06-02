package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.common.FetchStrategy.*
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status.*
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class RecipeRepositoryImpl @Inject constructor(
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>,
    private val savedRecipeDao: Provider<SavedRecipeDao>,
    private val recipeSearchCacheDao: Provider<RecipeSearchCacheDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : RecipeRepository {

    init {
        println("[OnHand] Creating ${this.javaClass.simpleName}")
    }

    override suspend fun findRecipes(
        fetchStrategy: FetchStrategy,
        ingredients: List<String>
    ): Resource<List<RecipePreview>> {
        println("[OnHand] findRecipes($fetchStrategy, $ingredients)")

        return when (fetchStrategy) {
            DATABASE -> {
                try {
                    Resource.success(data = getCachedRecipeSearchResults())
                } catch (e: Exception) {
                    // TODO: log analytics here
                    // TODO: rethrow in debug
                    println("[OnHand] Error retrieving cached recipes: ${e.message}")
                    Resource.error(msg = e.message.toString())
                }
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
                            data = findRecipes(
                                fetchStrategy = DATABASE,
                                ingredients
                            ).data
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

    override suspend fun saveRecipePreview(
        recipePreview: RecipePreview
    ) {
        println("[OnHand] saveRecipe($recipePreview)")
        savedRecipeDao
            .get()
            .addRecipe(recipePreview.toSavedRecipeEntity())
    }

    override suspend fun saveFullRecipe(
        recipe: FullRecipe
    ): Resource<Unit> {
        println("[OnHand] saveCustomRecipe($recipe)")
        return try {
            savedRecipeDao
                .get()
                .addRecipe(createCustomSavedRecipeEntity(recipe))
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: log analytics here
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
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
            .getAll()
            .map { it.map(SavedRecipeEntity::asRecipePreview) }
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

    override suspend fun getFullRecipe(id: Int): Resource<FullRecipe> {
        println("[OnHand] getCustomRecipeDetail($id)")
        return withContext(ioDispatcher) {
            try {
                if (isRecipeCustom(id)) {
                    Resource.success(
                        data = savedRecipeDao
                            .get()
                            .getRecipe(id)
                            .asFullRecipe()
                    )
                } else {
                    val detail = getRecipeDetail(id)

                    when (detail.status) {
                        SUCCESS -> {
                            // This won't work if the search cache changed zzzzzzzz...
                            val preview = getCachedRecipePreview(id)
                            Resource.success(
                                data = FullRecipe(
                                    preview = preview,
                                    // Shouldn't be null at this point, but if it is we throw the
                                    // NPE and catch it.
                                    detail = detail.data!!
                                )
                            )
                        }
                        ERROR -> {
                            Resource.error(msg = detail.message.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                // TODO: rethrow in debug
                Resource.error(msg = e.message.toString())
            }
        }
    }

    private suspend fun getCachedRecipePreview(id: Int): RecipePreview {
        return recipeSearchCacheDao
            .get()
            .getRecipe(id)
            .asRecipePreview()
    }

    override suspend fun isRecipeCustom(id: Int): Boolean {
        println("[OnHand] isRecipeCustom($id)")
        return savedRecipeDao
            .get()
            .isRecipeCustom(id) == 1
    }

    private suspend fun getCachedRecipeSearchResults(): List<RecipePreview> {
        return recipeSearchCacheDao
            .get()
            .getRecipeSearchResult()
            .map(RecipeSearchCacheEntity::asRecipePreview)
    }

    private suspend fun cacheRecipeSearchResults(recipePreviews: List<RecipePreview>) {
        recipeSearchCacheDao
            .get()
            .cacheRecipeSearchResult(
                recipePreviews.map(RecipePreview::toSearchCacheEntity)
            )
    }
}

// TODO: potentially move to more appropriate spot...
private fun NetworkRecipeDetail.asExternalModel() = RecipeDetail(
    instructions = instructions ?: "No instructions provided." // TODO: revisit
)

private fun NetworkRecipe.asExternalModel() = RecipePreview(
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
