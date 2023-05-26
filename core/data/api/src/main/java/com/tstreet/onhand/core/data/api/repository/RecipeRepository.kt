package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.common.FetchStrategy
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.core.model.SaveableRecipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun findRecipes(
        fetchStrategy: FetchStrategy,
        ingredients: List<String>
    ): Resource<List<Recipe>>

    suspend fun getRecipeDetail(id: Int): Resource<RecipeDetail>

    suspend fun saveRecipe(recipe: Recipe, isCustomRecipe : Boolean = false)

    suspend fun unsaveRecipe(id: Int)

    suspend fun isRecipeSaved(id: Int): Boolean

    fun getSavedRecipes(): Flow<List<SaveableRecipe>>
    suspend fun updateSavedRecipesMissingIngredient(ingredient: Ingredient)
    suspend fun updateSavedRecipesUsingIngredient(ingredient: Ingredient)
}
