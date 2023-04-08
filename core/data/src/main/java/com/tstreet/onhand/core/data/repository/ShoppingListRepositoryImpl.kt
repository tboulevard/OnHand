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

    override suspend fun insertShoppingList(shoppingList: List<ShoppingListIngredient>) {
        shoppingListDao
            .get()
            .insertShoppingList(shoppingList.map(ShoppingListIngredient::asEntity))
    }

    override fun getShoppingList(): Flow<List<ShoppingListIngredient>> {
        return shoppingListDao
            .get()
            .getShoppingList()
            .map { it.map(ShoppingListEntity::toExternalModel) }
    }

    override fun getShoppingListByRecipe(): List<ShoppingListIngredient> {
        TODO("Not yet implemented")
    }
}