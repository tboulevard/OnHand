package com.tstreet.onhand.core.data.api.repository

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {

    fun getShoppingList(): Flow<Resource<List<ShoppingListIngredient>>>

    fun getRecipesInShoppingList(): Flow<Resource<List<RecipePreview>>>

    suspend fun insertIngredients(shoppingList: List<ShoppingListIngredient>): Resource<Unit>

    suspend fun checkOffIngredient(ingredient: ShoppingListIngredient): Resource<Unit>

    suspend fun uncheckIngredient(ingredient: ShoppingListIngredient): Resource<Unit>

    suspend fun isIngredientCheckedOff(name: String): Boolean

    suspend fun isEmpty(): Boolean

    suspend fun removeRecipePreview(recipePreview : RecipePreview): Resource<Unit>

    suspend fun removeIngredient(ingredient : ShoppingListIngredient) : Resource<Unit>
}
