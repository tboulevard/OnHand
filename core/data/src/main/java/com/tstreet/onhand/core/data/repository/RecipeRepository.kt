package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import com.tstreet.onhand.core.network.model.NetworkRecipeIngredient
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    // TODO: look into whether API can accept list of ids instead of strings
    suspend fun findRecipes(fetchStrategy: FetchStrategy, ingredients: List<String>): List<Recipe>

    fun getRecipeDetail(id: Int): Flow<RecipeDetail>

    // TODO: model `Saveable`RecipeDetail or similar
    suspend fun saveRecipe(recipe: Recipe)

    suspend fun unsaveRecipe(id: Int)

    suspend fun isRecipeSaved(id: Int): Boolean

    fun getSavedRecipes(): Flow<List<SaveableRecipe>>
}

// TODO: move to more appropriate spot
fun NetworkRecipe.asExternalModel() = Recipe(
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

// TODO: move to more appropriate spot
fun NetworkRecipeIngredient.asExternalModel() = RecipeIngredient(
    Ingredient(
        id = id,
        name = name,
    ),
    image = image,
    amount = amount,
    unit = unit,
)

// TODO: move to more appropriate spot
fun NetworkRecipeDetail.asExternalModel() = RecipeDetail(
    id = id,
    // TODO: Determine whether it's best to just transmit an empty src url or some other state
    sourceUrl = sourceUrl ?: ""
)
