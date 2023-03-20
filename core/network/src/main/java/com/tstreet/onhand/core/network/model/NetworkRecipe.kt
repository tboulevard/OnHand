package com.tstreet.onhand.core.network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkRecipe(
    val id : Int,
    val title : String,
    val image : String,
    val imageType : String,
    val usedIngredientCount : Int,
    val missedIngredientCount : Int,
    val missedIngredients : List<RecipeSearchIngredient>,
    val usedIngredients : List<RecipeSearchIngredient>,
    val unusedIngredients : List<RecipeSearchIngredient>,
    val likes : Int
)