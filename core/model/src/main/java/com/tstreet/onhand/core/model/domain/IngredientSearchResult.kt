package com.tstreet.onhand.core.model.domain

import com.tstreet.onhand.core.model.Ingredient

sealed interface IngredientSearchResult {

    object Loading : IngredientSearchResult
    class Content(val ingredients: List<Ingredient>) : IngredientSearchResult
    object Empty : IngredientSearchResult
    object Error : IngredientSearchResult
}