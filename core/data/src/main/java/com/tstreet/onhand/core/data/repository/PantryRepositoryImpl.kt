package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Ingredient
import javax.inject.Inject

class PantryRepositoryImpl @Inject constructor() : PantryRepository {
    override fun addIngredient(ingredient: Ingredient) {
        println("[OnHand] Adding $ingredient to DB")
    }

    override fun removeIngredient(ingredient: Ingredient) {
        println("[OnHand] Removing $ingredient from DB")
    }
}