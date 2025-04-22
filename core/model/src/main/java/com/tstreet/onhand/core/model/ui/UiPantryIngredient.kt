package com.tstreet.onhand.core.model.ui

import androidx.compose.runtime.MutableState
import com.tstreet.onhand.core.model.data.Ingredient

data class UiPantryIngredient(
    val ingredient: Ingredient,
    val inPantry: MutableState<Boolean>
)