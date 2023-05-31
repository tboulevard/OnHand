package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.model.*
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun findRecipes(
        fetchStrategy: FetchStrategy,
        ingredients: List<String>
    ): Resource<List<Recipe>>

    suspend fun getRecipeDetail(id: Int): Resource<RecipeDetail>

    suspend fun saveRecipePreview(recipe: Recipe)
    suspend fun saveFullRecipe(
        recipe: FullRecipe
    )

    suspend fun unsaveRecipe(id: Int)
    suspend fun isRecipeSaved(id: Int): Boolean
    fun getSavedRecipes(): Flow<List<SaveableRecipe>>
    suspend fun updateSavedRecipesMissingIngredient(ingredient: Ingredient)
    suspend fun updateSavedRecipesUsingIngredient(ingredient: Ingredient)
    suspend fun getFullRecipe(id: Int): Resource<FullRecipe>

    suspend fun getCachedRecipePreview(id: Int): Resource<Recipe>
}
