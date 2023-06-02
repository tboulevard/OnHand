package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.model.*
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun findRecipes(
        fetchStrategy: FetchStrategy,
        ingredients: List<String>
    ): Resource<List<RecipePreview>>

    suspend fun getRecipeDetail(id: Int): Resource<RecipeDetail>

    suspend fun saveRecipePreview(recipePreview: RecipePreview)

    suspend fun saveFullRecipe(recipe: FullRecipe): Resource<Unit>

    suspend fun unsaveRecipe(id: Int)

    suspend fun isRecipeSaved(id: Int): Boolean

    fun getSavedRecipes(): Flow<List<SaveableRecipePreview>>

    suspend fun updateSavedRecipesMissingIngredient(ingredient: Ingredient)

    suspend fun updateSavedRecipesUsingIngredient(ingredient: Ingredient)

    suspend fun getFullRecipe(id: Int): Resource<FullRecipe>

    suspend fun isRecipeCustom(id: Int): Boolean
}
