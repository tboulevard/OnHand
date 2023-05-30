package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetail(
    // TODO: might be worth pulling actual ingredient list up into this model from RecipePreview
    val instructions: String?,
)