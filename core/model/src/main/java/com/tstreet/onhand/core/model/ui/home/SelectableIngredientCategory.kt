package com.tstreet.onhand.core.model.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.model.data.IngredientCategory

data class SelectableIngredientCategory(
    val category: IngredientCategory,
    val isSelected: MutableState<Boolean>
)

data class SelectedIngredientCategoryState(
    val categories: List<SelectableIngredientCategory>
) {
    companion object {
        val default: SelectedIngredientCategoryState =
            SelectedIngredientCategoryState(
                categories = IngredientCategory.entries.map {
                    SelectableIngredientCategory(
                        it,
                        if (it == IngredientCategory.ALL) mutableStateOf(true) else mutableStateOf(
                            false
                        )
                    )
                }
            )

    }

    fun getCategory(category: IngredientCategory) =
        default.categories.find { it.category == category }!!
}