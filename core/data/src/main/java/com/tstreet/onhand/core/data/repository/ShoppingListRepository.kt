package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {

    fun getShoppingList(): Flow<List<ShoppingListIngredient>>

    suspend fun insertShoppingList(shoppingList: List<ShoppingListIngredient>)

    suspend fun markIngredientPurchased(ingredient: ShoppingListIngredient)

    suspend fun unmarkIngredientPurchased(ingredient: ShoppingListIngredient)

    suspend fun isIngredientPurchased(id: Int): Boolean

    // TODO:
    fun getShoppingListByRecipe(): List<ShoppingListIngredient>

    suspend fun clear()
}
