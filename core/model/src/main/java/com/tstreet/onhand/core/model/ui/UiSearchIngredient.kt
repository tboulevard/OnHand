package com.tstreet.onhand.core.model.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.model.data.Ingredient

data class UiSearchIngredient(
    val ingredient: Ingredient,
    val inPantry: MutableState<Boolean>,
    val isSelected: MutableState<Boolean> = mutableStateOf(false)
)