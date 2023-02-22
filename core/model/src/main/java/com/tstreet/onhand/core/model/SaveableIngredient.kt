package com.tstreet.onhand.core.model

data class SaveableIngredient(
    val ingredient: Ingredient,
    val isInPantry: Boolean
) {
}