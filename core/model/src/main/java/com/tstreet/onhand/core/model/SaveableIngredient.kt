package com.tstreet.onhand.core.model

// TODO: potentially use to distinguish between an ingredient used only for display vs one
// we may want to save to DB...
data class SaveableIngredient(
    val ingredient: Ingredient,
    val isInPantry: Boolean
)
