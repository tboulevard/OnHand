package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.core.model.SaveableRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    // TODO: look into whether API can accept list of ids instead of strings
    suspend fun findRecipes(ingredients: List<String>): List<SaveableRecipe>

    fun getRecipeDetail(id: Int): Flow<RecipeDetail>

    // TODO: model `Saveable`RecipeDetail or similar
    suspend fun saveRecipe(recipeDetail: RecipeDetail)

    suspend fun unSaveRecipe(id: Int)
}

// TODO: move to more appropriate spot
fun NetworkRecipe.asExternalModel() =
    SaveableRecipe(
        Recipe(
            id = id,
            title = title,
            image = image,
            imageType = imageType,
            usedIngredientCount = usedIngredientCount,
            missedIngredientCount = missedIngredientCount,
            likes = likes
        ),
        isSaved = false
    )

// TODO: move to more appropriate spot
fun NetworkRecipeDetail.asExternalModel() = RecipeDetail(
    id = id,
    // TODO: Determine whether it's best to just transmit an empty src url or some other state
    sourceUrl = sourceUrl ?: EMPTY_SOURCE_URL
)

private const val EMPTY_SOURCE_URL = ""
