package com.tstreet.onhand.core.network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkRecipeDetail(
    val id: Int,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val glutenFree: Boolean,
    val dairyFree: Boolean,
    val veryHealthy: Boolean,
    val cheap: Boolean,
    val veryPopular: Boolean,
    val sustainable: Boolean,
    val lowFodmap: Boolean,
    val gaps: String?,
    val preparationMinutes: Int?,
    val cookingMinutes: Int?,
    val aggregateLikes: Int?,
    val healthScore: Float?,
    val creditsText: String?,
    val sourceName: String?,
    val pricePerServing: Double,
    val title: String?,
    // TODO: use this in PR when hooking up time to recipe search cards
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String?,
    val image: String?,
    val imageType: String?,
    val summary: String?,
    val cuisines: List<String>,
    val diets: List<String>,
    val occasions: List<String>,
    val instructions: String?
)
