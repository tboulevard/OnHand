package com.tstreet.onhand.core.model

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int,
    val likes: Int
)

data class SaveableRecipe(
    val recipe : Recipe,
    val isSaved : Boolean
)
