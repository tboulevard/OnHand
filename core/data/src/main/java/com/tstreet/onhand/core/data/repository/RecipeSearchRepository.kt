package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.network.model.NetworkRecipe

interface RecipeSearchRepository {
    suspend fun searchRecipes(ingredients: List<String>): List<Recipe>
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