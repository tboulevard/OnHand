package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetail(
    val id: Int,
    val sourceUrl: String
)
