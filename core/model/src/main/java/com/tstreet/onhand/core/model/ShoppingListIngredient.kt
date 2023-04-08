package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
class ShoppingListIngredient(
    val id: Int,
    val name : String,
    // TODO: Revisit, quantity/unit of measure later
    val quantity : Int,
    val unitOfMeasure : String,
    val mappedRecipes : List<Recipe> = emptyList() // TODO:
)