package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable
class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val usedIngredients: List<Ingredient>,
    val missedIngredientCount: Int,
    val missedIngredients: List<Ingredient>,
    val likes: Int
)
data class SaveableRecipe(
    val recipe: Recipe,
    val isSaved: Boolean,
)
