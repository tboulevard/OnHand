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
    val missedIngredients : List<NetworkRecipeIngredient>,
    val usedIngredients : List<NetworkRecipeIngredient>,
    val unusedIngredients : List<NetworkRecipeIngredient>,
    val likes : Int
)
