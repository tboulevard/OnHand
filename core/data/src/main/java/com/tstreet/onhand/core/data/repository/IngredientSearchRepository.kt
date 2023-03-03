package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Ingredient

interface IngredientSearchRepository {
    fun searchIngredients(prefix: String): List<Ingredient>
}