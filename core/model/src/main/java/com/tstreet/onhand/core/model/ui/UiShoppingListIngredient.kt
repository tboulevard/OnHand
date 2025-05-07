package com.tstreet.onhand.core.model.ui

import androidx.compose.runtime.MutableState
import com.tstreet.onhand.core.model.data.ShoppingListIngredient

class UiShoppingListIngredient(
    val ingredient: ShoppingListIngredient,
    val isChecked: MutableState<Boolean>
)