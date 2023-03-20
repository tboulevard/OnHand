package com.tstreet.onhand.core.network.model

import kotlinx.serialization.Serializable

@Serializable
class RecipeSearchIngredient(
    val id: Int,
    val amount: Double,
    val unit: String,
    val unitLong: String,
    val unitShort: String,
    val aisle: String?,
    val name : String,
    val original : String,
    val originalName : String,
    val meta: List<String>,
    val image : String
)