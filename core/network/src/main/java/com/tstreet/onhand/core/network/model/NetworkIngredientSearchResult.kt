package com.tstreet.onhand.core.network.model

import kotlinx.serialization.Serializable

@Serializable
class NetworkIngredientSearchResult(
    val results: List<NetworkIngredient>,
    val offset : Int,
    val number : Int,
    val totalResults : Int,
)
