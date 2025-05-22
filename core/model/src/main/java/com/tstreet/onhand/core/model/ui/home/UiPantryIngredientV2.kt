package com.tstreet.onhand.core.model.ui.home

import androidx.compose.runtime.MutableState
import com.tstreet.onhand.core.model.data.IngredientCategory

data class UiPantryIngredientV2(
    val ingredientName: String,
    val category: IngredientCategory,
    val inPantry: MutableState<Boolean>,
    val inShoppingCart: MutableState<Boolean>
)