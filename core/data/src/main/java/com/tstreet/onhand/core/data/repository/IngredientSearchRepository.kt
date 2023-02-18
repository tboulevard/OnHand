package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.network.model.NetworkIngredient

interface IngredientSearchRepository {
    fun searchIngredients(prefix: String): List<NetworkIngredient>
}