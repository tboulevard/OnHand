package com.tstreet.onhand.core.model

/**
 * External model representation for an Ingredient in a given Recipe.
 */
data class Ingredient(
    val name : String,
    // TODO: Path only, refactor once we need images
    val image : String,
    // TODO: Refactor if needed
    val childIngredient: ChildIngredient? = null
)
