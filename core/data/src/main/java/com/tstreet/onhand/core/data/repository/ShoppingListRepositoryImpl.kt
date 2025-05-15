package com.tstreet.onhand.core.data.repository

import android.util.Log
import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.domain.repository.ShoppingListRepository
import com.tstreet.onhand.core.database.dao.ShoppingListDao
import com.tstreet.onhand.core.database.model.*
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.ShoppingListIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ShoppingListRepositoryImpl @Inject constructor(
    private val shoppingListDao: Provider<ShoppingListDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : ShoppingListRepository {

    init {
        Log.d("[OnHand]", "Creating ${this.javaClass.simpleName}")
    }

    override suspend fun getShoppingList(): Resource<List<ShoppingListIngredient>> {
        return withContext(ioDispatcher) {
            try {
                Resource.success(
                    shoppingListDao
                        .get()
                        .getShoppingList()
                        .map(ShoppingListEntity::toExternalModel)
                )
            } catch (e: Exception) {
                Log.d("[OnHand]", "Error getting to shopping list - ${e.message}")
                Resource.error(msg = e.message.toString())
            }
        }
    }

    override suspend fun addIngredients(
        shoppingList: List<ShoppingListIngredient>
    ): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                shoppingListDao
                    .get()
                    .insertShoppingList(shoppingList.map(ShoppingListIngredient::asEntity))
                Resource.success(null)
            } catch (e: Exception) {
                // TODO: rethrow in debug
                Log.d("[OnHand]", "Error adding to shopping list - ${e.message}")
                Resource.error(msg = e.message.toString())
            }
        }
    }

    override suspend fun removeIngredient(ingredient: ShoppingListIngredient): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                shoppingListDao
                    .get()
                    .removeIngredient(ingredient.ingredient.id)
                Resource.success(null)
            } catch (e: Exception) {
                // TODO: rethrow in debug
                Log.d("[OnHand]", "Error removing $ingredient from Shopping List - ${e.message}")
                Resource.error(msg = e.message.toString())
            }
        }
    }

    override suspend fun getRecipesInShoppingList(): Resource<List<RecipePreview>> {
        return withContext(ioDispatcher) {
            try {
                Resource.success(
                    shoppingListDao
                        .get()
                        .getRecipesInShoppingList()
                        .mapNotNull { it }
                )
            } catch (e: Exception) {
                Log.d("[OnHand]", "Error getting to shopping list - ${e.message}")
                Resource.error(msg = e.message.toString())
            }
        }
    }

    override suspend fun removeRecipePreview(recipePreview: RecipePreview): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                shoppingListDao.get().removeRecipePreview(recipePreview)
                Resource.success(null)
            } catch (e: Exception) {
                Log.d("[OnHand]", "Error removing recipe preview - ${e.message}")
                Resource.error(msg = e.message.toString())
            }
        }
    }

    override suspend fun isEmpty(): Boolean {
        return withContext(ioDispatcher) {
            shoppingListDao
                .get()
                .isEmpty()
        }
    }
}
