package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val usedIngredients: List<Ingredient> = emptyList(),
    val missedIngredientCount: Int,
    val missedIngredients: List<Ingredient> = emptyList(),
    val likes: Int
)

@Serializable
data class SaveableRecipe(
    val recipe: Recipe,
    val isSaved: Boolean,
)
