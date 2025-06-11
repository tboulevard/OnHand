package com.tstreet.onhand.core.model.ui.home

import androidx.compose.runtime.MutableState
import com.tstreet.onhand.core.model.data.IngredientCategory

data class SelectableIngredientCategory(
    val category: IngredientCategory,
    val isSelected: MutableState<Boolean>
)