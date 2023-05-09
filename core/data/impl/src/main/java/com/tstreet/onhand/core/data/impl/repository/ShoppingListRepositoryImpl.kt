package com.tstreet.onhand.core.data.impl.repository

import com.tstreet.onhand.core.common.Resource
import com.tstreet.onhand.core.data.api.repository.ShoppingListRepository
import com.tstreet.onhand.core.database.dao.ShoppingListDao
import com.tstreet.onhand.core.database.model.asEntity
import com.tstreet.onhand.core.database.model.toExternalModel
import com.tstreet.onhand.core.model.ShoppingListIngredient
import javax.inject.Inject
import javax.inject.Provider

class ShoppingListRepositoryImpl @Inject constructor(
    private val shoppingListDao: Provider<ShoppingListDao>
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

    override suspend fun clear() {
        shoppingListDao.get().clear()
    }

    override suspend fun isEmpty(): Boolean {
        return shoppingListDao
            .get()
            .isEmpty()
    }

    override fun getShoppingListByRecipe(): List<ShoppingListIngredient> {
        TODO("Not yet implemented")
    }
}
