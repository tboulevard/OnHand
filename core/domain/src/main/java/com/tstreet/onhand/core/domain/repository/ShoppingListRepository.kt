package com.tstreet.onhand.core.domain.repository

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.ShoppingListIngredient

interface ShoppingListRepository {

    suspend fun getShoppingList(): Resource<List<ShoppingListIngredient>>

    suspend fun getRecipesInShoppingList(): Resource<List<RecipePreview>>

    suspend fun addIngredients(shoppingList: List<ShoppingListIngredient>): Resource<Unit>

    suspend fun isEmpty(): Boolean

    suspend fun removeRecipePreview(recipePreview : RecipePreview): Resource<Unit>

    suspend fun removeIngredient(ingredient : ShoppingListIngredient) : Resource<Unit>
}
