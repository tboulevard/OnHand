package com.tstreet.onhand.core.data.repository

import com.tstreet.onhand.core.database.dao.ShoppingListDao
import com.tstreet.onhand.core.database.model.ShoppingListEntity
import com.tstreet.onhand.core.database.model.asEntity
import com.tstreet.onhand.core.database.model.toExternalModel
import com.tstreet.onhand.core.model.ShoppingListIngredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class ShoppingListRepositoryImpl @Inject constructor(
    private val shoppingListDao: Provider<ShoppingListDao>
) : ShoppingListRepository {

    override fun getShoppingList(): Flow<List<ShoppingListIngredient>> {
        return shoppingListDao
            .get()
            .getShoppingList()
            .map { it.map(ShoppingListEntity::toExternalModel) }
    }

    override suspend fun insertShoppingList(shoppingList: List<ShoppingListIngredient>) {
        shoppingListDao
            .get()
            .insertShoppingList(shoppingList.map(ShoppingListIngredient::asEntity))
    }

    override suspend fun markIngredientPurchased(ingredient: ShoppingListIngredient) {
        shoppingListDao
            .get()
            .markIngredientPurchased(ingredient.id)
    }

    override suspend fun unmarkIngredientPurchased(ingredient: ShoppingListIngredient) {
        shoppingListDao
            .get()
            .unmarkIngredientPurchased(ingredient.id)
    }

    override suspend fun isIngredientPurchased(id: Int): Boolean {
        return shoppingListDao
            .get()
            .isShoppingListIngredientPurchased(id)
    }

    override suspend fun clear() {
        shoppingListDao.get().clear()
    }

    override fun getShoppingListByRecipe(): List<ShoppingListIngredient> {
        TODO("Not yet implemented")
    }
}
