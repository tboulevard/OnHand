package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {

    suspend fun insertShoppingList(shoppingList: List<ShoppingListIngredient>)

    fun getShoppingList(): Flow<List<ShoppingListIngredient>>

    // TODO:
    fun getShoppingListByRecipe(): List<ShoppingListIngredient>
}