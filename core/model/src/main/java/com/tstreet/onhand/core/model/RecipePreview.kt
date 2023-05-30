package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
class RecipePreview(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val usedIngredients: List<RecipeIngredient>,  // TODO: May not be necessary to pass around in preview
    val missedIngredientCount: Int,
    val missedIngredients: List<RecipeIngredient>, // TODO: May not be necessary to pass around in preview
    val likes: Int,
    val isCustom : Boolean
)