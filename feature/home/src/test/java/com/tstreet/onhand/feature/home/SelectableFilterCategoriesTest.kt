package com.tstreet.onhand.feature.home

import com.tstreet.onhand.core.model.data.IngredientCategory
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class SelectableFilterCategoriesTest {
    
    private val manager = CategoryFilterSelectionManager()

    @Test
    fun whenAllSelectedAndAnotherCategorySelected_allIsDeselectedAndOtherCategoryIsSelected() {
        // When - select a non-ALL category (ALL is selected by default)
        val produceCategory = manager.selectableCategories.find {
            it.category == IngredientCategory.PRODUCE 
        }!!
        manager.onSelected(produceCategory)
        
        // Then
        val allCategory = manager.selectableCategories.find {
            it.category == IngredientCategory.ALL 
        }
        
        assertFalse("ALL category should be deselected", allCategory?.isSelected?.value!!)
        assertTrue("PRODUCE category should be selected", produceCategory.isSelected.value)
    }

    @Test
    fun whenCategorySelected_currentIsDeselectedNewCategoryIsSelected() {
        // Given
        val produceCategory = manager.selectableCategories.find {
            it.category == IngredientCategory.PRODUCE 
        }!!
        manager.onSelected(produceCategory) // First select PRODUCE
        
        // When - select a different category
        val dairyCategory = manager.selectableCategories.find {
            it.category == IngredientCategory.DAIRY_AND_EGGS 
        }!!
        manager.onSelected(dairyCategory)
        
        // Then
        assertFalse("PRODUCE category should be deselected", produceCategory.isSelected.value)
        assertTrue("DAIRY_AND_EGGS category should be selected", dairyCategory.isSelected.value)
    }

    @Test
    fun whenOneNonAllCategorySelectedAndThatCategoryIsDeselected_allCategoryIsSelected() {
        // Given
        val produceCategory = manager.selectableCategories.find {
            it.category == IngredientCategory.PRODUCE 
        }!!
        manager.onSelected(produceCategory) // First select PRODUCE
        
        // When - deselect the same category (clicking it again)
        manager.onSelected(produceCategory)
        
        // Then
        val allCategory = manager.selectableCategories.find {
            it.category == IngredientCategory.ALL 
        }
        
        assertTrue("ALL category should be selected", allCategory?.isSelected?.value!!)
        assertFalse("PRODUCE category should be deselected", produceCategory.isSelected.value)
    }
}
