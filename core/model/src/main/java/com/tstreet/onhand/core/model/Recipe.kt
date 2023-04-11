package com.tstreet.onhand.core.model

class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val usedIngredients: List<RecipeIngredient>,
    val missedIngredientCount: Int,
    val missedIngredients: List<RecipeIngredient>,
    val likes: Int
)

data class SaveableRecipe(
    val recipe: Recipe,
    val isSaved: Boolean,
)
