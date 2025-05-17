package com.tstreet.onhand.core.model.ui

import com.tstreet.onhand.core.model.data.Ingredient

data class SelectableIngredient(
    val ingredient: Ingredient,
    val isSelected: Boolean,
    val onSelect: ((Ingredient) -> Unit)? = null
)