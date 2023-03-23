package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.network.model.NetworkRecipe
import kotlinx.coroutines.flow.Flow

interface RecipeSearchRepository {
    // TODO: look into whether API can accept list of ids instead of strings
    fun searchRecipes(ingredients: List<String>): Flow<List<Recipe>>
}

// TODO: move to more appropriate spot
fun NetworkRecipe.asExternalModel() = Recipe(
    id = id,
    title = title,
    image = image,
    imageType = imageType,
    usedIngredientCount = usedIngredientCount,
    missedIngredientCount = missedIngredientCount,
    likes = likes
)
