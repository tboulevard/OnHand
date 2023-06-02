package com.tstreet.onhand.feature.ingredientsearch

import com.tstreet.onhand.core.model.Ingredient

data class SelectableIngredient(
    val ingredient: Ingredient,
    val isSelected: Boolean
)
