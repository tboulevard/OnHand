package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {

    fun getShoppingList(): Flow<List<ShoppingListIngredient>>

    suspend fun insertIngredients(shoppingList: List<ShoppingListIngredient>)

    suspend fun checkOffIngredient(ingredient: ShoppingListIngredient)

    suspend fun uncheckIngredient(ingredient: ShoppingListIngredient)

    suspend fun isIngredientCheckedOff(name: String): Boolean

    fun getShoppingListByRecipe(): List<ShoppingListIngredient>

    suspend fun isEmpty(): Boolean

    suspend fun clear()
}
