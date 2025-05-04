package com.tstreet.onhand.core.model.ui

import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.ShoppingListIngredient

sealed class ShoppingListUiState(
    val ingredients: List<ShoppingListIngredient>,
    val recipePreviews: List<RecipePreview>
) {
    object Loading : ShoppingListUiState(emptyList(), emptyList())

    // TODO: refactor duplicate screen content functions...
    data class Success(
        val shoppingListIngredients: List<ShoppingListIngredient>,
        val mappedRecipePreviews: List<RecipePreview>
    ) : ShoppingListUiState(shoppingListIngredients, mappedRecipePreviews)

    data class Error(
        val message: String,
        val shoppingListIngredients: List<ShoppingListIngredient>,
        val mappedRecipePreviews: List<RecipePreview>
    ) : ShoppingListUiState(shoppingListIngredients, mappedRecipePreviews)

    fun screenContent() = listOf(
        ShoppingListItem.Header(
            text = "Shopping List"
        ),
        ShoppingListItem.Summary(
            numberOfRecipes = recipePreviews.size,
            numberOfIngredients = ingredients.size
        ),
        ShoppingListItem.MappedRecipes(
            recipePreviews = recipePreviews
        ),
        ShoppingListItem.Ingredients(
            ingredients = ingredients
        )
    )
}