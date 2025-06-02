package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class CategoryFilterSelectionManager @Inject constructor() {

    /**
     * List of selectable elements as [SelectableIngredientCategory].
     */
    val selectableCategories = IngredientCategory.allEntries.map { entry ->
        SelectableIngredientCategory(
            entry,
            if (entry == IngredientCategory.ALL) mutableStateOf(true) else mutableStateOf(
                false
            )
        )
    }

    /**
     * Currently selected [IngredientCategory].
     */
    private val selected =
        MutableStateFlow(getCategory(IngredientCategory.ALL))

    /**
     * Handles all selection logic - currently only supports selection of one category at a time.
     *
     * If selected category is de-selected, we revert to [IngredientCategory.ALL] (i.e. no filter).
     */
    fun onSelected(new: SelectableIngredientCategory) {
        val currentlySelectedCategory = selected.value.category
        val newCategory = new.category

        if (currentlySelectedCategory == IngredientCategory.ALL) {
            if (newCategory != IngredientCategory.ALL) {
                // Another category is selected, deselect all and select that category
                markCategory(IngredientCategory.ALL, selected = false)
                markCategory(newCategory, selected = true)
                selected.tryEmit(new)
            }
        } else {
            if (selected.value == new) {
                // Non-all category deselected and it is the only category, select ALL
                markCategory(newCategory, selected = false)
                val markedCategory = markCategory(IngredientCategory.ALL, selected = true)
                selected.tryEmit(markedCategory)
            } else {
                // Otherwise de-select currently selected
                markCategory(currentlySelectedCategory, selected = false)
                markCategory(newCategory, selected = true)
                selected.tryEmit(new)
            }
        }
    }

    fun <T> observeSelected(
        mappedFlow: (IngredientCategory) -> Flow<T>
    ) = selected.flatMapLatest { mappedFlow(it.category) }

    /**
     * Mark [category] within [selectableCategories] as [selected]. Returns the marked category,
     * which can optionally be used.
     */
    private fun markCategory(
        category: IngredientCategory,
        selected: Boolean
    ): SelectableIngredientCategory {
        val retrievedCategory = getCategory(category)
        retrievedCategory.isSelected.value = selected
        return retrievedCategory
    }

    /**
     * Gets the UI class, [SelectableIngredientCategory], associated with [IngredientCategory]
     */
    private fun getCategory(category: IngredientCategory) =
        selectableCategories.find { it.category == category }!!

}