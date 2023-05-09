package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.model.ShoppingListIngredient

interface ShoppingListRepository {

    suspend fun getShoppingList(): Resource<List<ShoppingListIngredient>>

    suspend fun insertIngredients(shoppingList: List<ShoppingListIngredient>): Resource<Unit>

    suspend fun checkOffIngredient(ingredient: ShoppingListIngredient)

    suspend fun uncheckIngredient(ingredient: ShoppingListIngredient)

    suspend fun isIngredientCheckedOff(name: String): Boolean

    fun getShoppingListByRecipe(): List<ShoppingListIngredient>

    suspend fun isEmpty(): Boolean

    suspend fun clear()
}
