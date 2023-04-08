package com.tstreet.onhand.core.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

/**
 * External model representation for an Ingredient in a given Recipe.
 */
@Stable
@Serializable
data class Ingredient(
    val id: Int,
    val name: String,
    // TODO: Path only, refactor once we need images
    val image: String? = null,
    // TODO: Refactor if needed
    val childIngredient: ChildIngredient? = null,
    val inPantry : Boolean = false
)
