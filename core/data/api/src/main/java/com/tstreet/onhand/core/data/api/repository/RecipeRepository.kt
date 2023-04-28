package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.core.model.SaveableRecipe
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
