package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient

interface ShoppingListRepository {

    suspend fun getShoppingList(): Resource<List<ShoppingListIngredient>>

    suspend fun getRecipesInShoppingList(): Resource<List<Recipe>>

    suspend fun insertIngredients(shoppingList: List<ShoppingListIngredient>): Resource<Unit>

    suspend fun checkOffIngredient(ingredient: ShoppingListIngredient): Resource<Unit>

    suspend fun uncheckIngredient(ingredient: ShoppingListIngredient): Resource<Unit>

    suspend fun isIngredientCheckedOff(name: String): Boolean

    suspend fun isEmpty(): Boolean

    suspend fun clear()
}
