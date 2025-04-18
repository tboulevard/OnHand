package com.tstreet.onhand.core.model.ui

import com.tstreet.onhand.core.model.Ingredient

data class SelectableIngredient(
    val ingredient: Ingredient,
    val isSelected: Boolean
)