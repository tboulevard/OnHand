package com.tstreet.onhand.core.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.common.FetchStrategy.*
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.common.Status.*
import com.tstreet.onhand.core.domain.repository.RecipeRepository
import com.tstreet.onhand.core.database.dao.RecipeSearchCacheDao
import com.tstreet.onhand.core.database.dao.SavedRecipeDao
import com.tstreet.onhand.core.database.model.*
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState
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
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val onHandNetworkDataSource: Provider<OnHandNetworkDataSource>,
    private val savedRecipeDao: Provider<SavedRecipeDao>,
    private val recipeSearchCacheDao: Provider<RecipeSearchCacheDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : RecipeRepository {

    init {
        Log.d("[OnHand]", "Creating ${this.javaClass.simpleName}")
    }

    override suspend fun findRecipes(
        fetchStrategy: FetchStrategy,
        ingredients: List<Ingredient>
    ): Resource<List<RecipePreview>> {
        Log.d("[OnHand]", "findRecipes($fetchStrategy, $ingredients)")
        return withContext(ioDispatcher) {
            when (fetchStrategy) {
                DATABASE -> {
                    try {
                        Resource.success(data = getCachedRecipeSearchResults())
                    } catch (e: Exception) {
                        // TODO: log analytics here
                        // TODO: rethrow in debug
                        Log.d("[OnHand]", "Error retrieving cached recipes: ${e.message}")
                        Resource.error(msg = e.message.toString())
                    }
                }

                NETWORK -> {
                    val networkResponse =
                        onHandNetworkDataSource
                            .get()
                            .findRecipesFromIngredients(ingredients.map { it.name })

                    when (networkResponse) {
                        is Success -> {
                            val externalModel =
                                networkResponse.body.map(NetworkRecipe::asExternalModel)

                            // TODO: Note, we rely on this step to merge previews into FullRecipes;
                            //  Evaluate later.
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
    }

    override suspend fun getRecipeDetail(id: Int): Resource<RecipeDetail> {
        Log.d("[OnHand]", "getRecipeDetail($id)")
        return withContext(ioDispatcher) {
            val networkResponse = onHandNetworkDataSource
                .get()
                .getRecipeDetail(id)

            when (networkResponse) {
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
    }

    override suspend fun saveRecipePreview(
        recipePreview: RecipePreview
    ): Resource<Unit> {
        return withContext(ioDispatcher) {
            val convertedEntity = recipePreview.toSavedRecipeEntity()
            Log.d("[OnHand]", "saveRecipe($convertedEntity)")
            try {
                savedRecipeDao
                    .get()
                    .addRecipe(convertedEntity)
                Resource.success(Unit)
            } catch (e: Exception) {
                // TODO: log analytics here
                // TODO: rethrow in debug
                Resource.error(msg = e.message.toString())
            }
        }
    }

    override suspend fun saveFullRecipe(
        fullRecipe: FullRecipe
    ): SaveRecipeResult {
        Log.d("[OnHand]", "saveCustomRecipe($fullRecipe)")
        return withContext(ioDispatcher) {
            try {
                savedRecipeDao
                    .get()
                    .addRecipe(fullRecipe.toSavedRecipeEntity())
                SaveRecipeResult.success(fullRecipe.preview.id)
            } catch (e: SQLiteConstraintException) {
                // TODO: log analytics here
                // TODO: rethrow in debug
                SaveRecipeResult.namingConflict(e.message)
            } catch (e: Exception) {
                // TODO: log analytics here
                // TODO: rethrow in debug
                SaveRecipeResult.unknownError(e.message)
            }
        }
    }

    override suspend fun unsaveRecipe(id: Int): Resource<Unit> {
        Log.d("[OnHand]", "unsaveRecipe($id)")
        return withContext(ioDispatcher) {
            try {
                savedRecipeDao
                    .get()
                    .deleteRecipe(id)
                Resource.success(Unit)
            } catch (e: Exception) {
                // TODO: log analytics here
                // TODO: rethrow in debug
                Resource.error(msg = e.message.toString())
            }
        }
    }

    override suspend fun isRecipeSaved(id: Int): Boolean {
        Log.d("[OnHand]", "isRecipeSaved($id)")
        return withContext(ioDispatcher) {
            savedRecipeDao
                .get()
                .isRecipeSaved(id) == 1
        }
    }

    override suspend fun isRecipeSaved(title: String): Boolean {
        Log.d("[OnHand]", "isRecipeSaved($title)")
        return withContext(ioDispatcher) {
            savedRecipeDao
                .get()
                .isRecipeSaved(title) == 1
        }
    }

    override fun getSavedRecipes(): Flow<List<RecipePreviewWithSaveState>> {
        Log.d("[OnHand]", "getSavedRecipes()")
        return savedRecipeDao
            .get()
            .getAll()
            .map { it.map(SavedRecipeEntity::asSaveableRecipePreview) }
    }

    override suspend fun updateSavedRecipesMissingIngredient(ingredient: Ingredient) {
        return withContext(ioDispatcher) {
            try {
                savedRecipeDao
                    .get()
                    .updateRecipesMissingIngredient(ingredient.name)
            } catch (e: Exception) {
                // TODO: log analytics here
                // TODO: rethrow in debug
            }
        }
    }

    override suspend fun updateSavedRecipesUsingIngredient(ingredient: Ingredient) {
        return withContext(ioDispatcher) {
            try {
                savedRecipeDao
                    .get()
                    .updateRecipesUsingIngredient(ingredient.name)
            } catch (e: Exception) {
                // TODO: log analytics here
                // TODO: rethrow in debug
            }
        }
    }

    override suspend fun getFullRecipe(id: Int): Resource<FullRecipe> {
        Log.d("[OnHand]", "getCustomRecipeDetail($id)")
        return withContext(ioDispatcher) {
            try {
                // Custom recipes have both preview and detail information stored locally
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
                            // Preview information from API sourced recipes will either be in the
                            // saved recipe DB or search cache, return the info from whichever
                            // its in.
                            // TODO: revisit above comment because it relies on the app to operate
                            //  a specific way to work...
                            val preview = if (isRecipeSaved(id)) {
                                getSavedRecipe(id).preview
                            } else {
                                getCachedRecipePreview(id)
                            }

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

    private suspend fun getSavedRecipe(id: Int): RecipePreviewWithSaveState {
        return withContext(ioDispatcher) {
            savedRecipeDao
                .get()
                .getRecipe(id)
                .asSaveableRecipePreview()
        }
    }

    private suspend fun getCachedRecipePreview(id: Int): RecipePreview {
        return withContext(ioDispatcher) {
            recipeSearchCacheDao
                .get()
                .getRecipe(id)
                .asRecipePreview()
        }
    }

    override suspend fun isRecipeCustom(id: Int): Boolean {
        Log.d("[OnHand]", "isRecipeCustom($id)")
        return withContext(ioDispatcher) {
            savedRecipeDao
                .get()
                .isRecipeCustom(id) == 1
        }
    }

    private suspend fun getCachedRecipeSearchResults(): List<RecipePreview> {
        return withContext(ioDispatcher) {
            recipeSearchCacheDao
                .get()
                .getRecipeSearchResult()
                .map(RecipeSearchCacheEntity::asRecipePreview)
        }
    }

    private suspend fun cacheRecipeSearchResults(recipePreviews: List<RecipePreview>) {
        return withContext(ioDispatcher) {
            recipeSearchCacheDao
                .get()
                .cacheRecipeSearchResult(
                    recipePreviews.map(RecipePreview::toSearchCacheEntity)
                )
        }
    }
}

// TODO: potentially move to more appropriate spot...
private fun NetworkRecipeDetail.asExternalModel() = RecipeDetail(
    instructions = instructions ?: "No instructions provided." // TODO: revisit
)

fun NetworkRecipe.asExternalModel() = RecipePreview(
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

private fun NetworkRecipeIngredient.asExternalModel() =
    Ingredient(
        id = id,
        name = name,
    )
