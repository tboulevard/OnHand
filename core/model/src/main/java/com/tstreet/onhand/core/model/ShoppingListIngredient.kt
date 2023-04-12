package com.tstreet.onhand.core.model

import kotlinx.serialization.Serializable

@Serializable
class ShoppingListIngredient(
    val id: Int,
    val name: String,
    // Because ingredients can be members of multiple recipes, with different units of measurement
    val recipeMeasures: List<RecipeMeasure>
)

/**
 * The measurements associated with an ingredient to satisfy the given [Recipe].
 */
@Serializable
data class RecipeMeasure(
    val recipe: Recipe,
    val unit: String,
    val amount: Double
)
