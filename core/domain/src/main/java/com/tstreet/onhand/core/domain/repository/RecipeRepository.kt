package com.tstreet.onhand.core.domain.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun findRecipes(
        fetchStrategy: FetchStrategy,
        ingredients: List<Ingredient>
    ): Resource<List<RecipePreview>>

    suspend fun getRecipeDetail(id: Int): Resource<RecipeDetail>

    suspend fun saveRecipePreview(recipePreview: RecipePreview)

    suspend fun saveFullRecipe(fullRecipe: FullRecipe): SaveRecipeResult

    suspend fun unsaveRecipe(id: Int)

    suspend fun isRecipeSaved(id: Int): Boolean

    suspend fun isRecipeSaved(title: String): Boolean

    fun getSavedRecipes(): Flow<List<RecipePreviewWithSaveState>>

    suspend fun updateSavedRecipesMissingIngredient(ingredient: Ingredient)

    suspend fun updateSavedRecipesUsingIngredient(ingredient: Ingredient)

    suspend fun getFullRecipe(id: Int): Resource<FullRecipe>

    suspend fun isRecipeCustom(id: Int): Boolean
}
