package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.common.CommonModule.IO
import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.database.dao.ShoppingListDao
import com.tstreet.onhand.core.database.model.asEntity
import com.tstreet.onhand.core.database.model.toExternalModel
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class ShoppingListRepositoryImpl @Inject constructor(
    private val shoppingListDao: Provider<ShoppingListDao>,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher,
) : ShoppingListRepository {

    override suspend fun getShoppingList(): Resource<List<ShoppingListIngredient>> {
        return try {
            Resource.success(
                data = shoppingListDao
                    .get()
                    .getShoppingList()
                    .map { it.toExternalModel() }
            )
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun getRecipesInShoppingList(): Resource<List<Recipe>> {
        return try {
            Resource.success(
                data = shoppingListDao
                    .get()
                    .getRecipesInShoppingList()
                    // SQL statement should guarantee non-null - .mapNotNull for compile safety
                    .mapNotNull { it }
            )
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun insertIngredients(
        shoppingList: List<ShoppingListIngredient>
    ): Resource<Unit> {
        return try {
            shoppingListDao
                .get()
                .insertShoppingList(shoppingList.map(ShoppingListIngredient::asEntity))
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun checkOffIngredient(ingredient: ShoppingListIngredient): Resource<Unit> {
        return try {
            shoppingListDao
                .get()
                .markIngredientPurchased(ingredient.name)
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun uncheckIngredient(ingredient: ShoppingListIngredient): Resource<Unit> {
        return try {
            shoppingListDao
                .get()
                .unmarkIngredientPurchased(ingredient.name)
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun isIngredientCheckedOff(name: String): Boolean {
        return shoppingListDao
            .get()
            .isShoppingListIngredientPurchased(name)
    }

    override suspend fun removeRecipe(recipe: Recipe): Resource<Unit> {
        return try {
            shoppingListDao.get().removeRecipe(recipe)
            Resource.success(null)
        } catch (e: Exception) {
            // TODO: rethrow in debug
            Resource.error(msg = e.message.toString())
        }
    }

    override suspend fun isEmpty(): Boolean {
        return shoppingListDao
            .get()
            .isEmpty()
    }

    override suspend fun removeIngredient(ingredient: ShoppingListIngredient): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                shoppingListDao
                    .get()
                    .removeIngredient(ingredient.name)
                Resource.success(null)
            } catch (e: Exception) {
                // TODO: rethrow in debug
                println("[OnHand] Error removing $ingredient from Shopping List, msg=${e.message}")
                Resource.error(msg = e.message.toString())
            }
        }
    }
}
