package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Ingredient

interface PantryRepository {
    // Add return value representing success/failure state of adding/removing from DB
    fun addIngredient(ingredient: Ingredient)
    fun removeIngredient(ingredient: Ingredient)
}