package com.tstreet.onhand.core.model.ui.home

data class HomeUiState(
    val filterCategories: List<SelectableIngredientCategory> = emptyList(),
    val pantryRows: List<PantryRowItem> = emptyList()
)