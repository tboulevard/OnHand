package com.tstreet.onhand.core.model.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.model.data.ShoppingListIngredient

class UiShoppingListIngredient(
    val ingredient: ShoppingListIngredient,
    val isChecked: MutableState<Boolean>,
    val isInCart: MutableState<Boolean> = mutableStateOf(true)
)