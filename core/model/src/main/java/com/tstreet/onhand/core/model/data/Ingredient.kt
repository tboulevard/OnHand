package com.tstreet.onhand.core.model.data

import kotlinx.serialization.Serializable


/**
 * External model representation for an Ingredient.
 */
@Serializable
data class Ingredient(
    val id: Int,
    val name: String,
    val category: IngredientCategory = IngredientCategory.randomCategory()
)
