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
    val missedIngredients : List<NetworkRecipeSearchIngredient>,
    val usedIngredients : List<NetworkRecipeSearchIngredient>,
    val unusedIngredients : List<NetworkRecipeSearchIngredient>,
    val likes : Int
)
