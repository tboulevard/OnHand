package com.tstreet.onhand.core.model.ui.home

import androidx.compose.runtime.MutableState
import com.tstreet.onhand.core.model.data.Ingredient

data class UiPantryIngredientV2(
    val ingredient: Ingredient,
    val inPantry: MutableState<Boolean>
)