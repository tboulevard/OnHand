package com.tstreet.onhand.core.database.dao;

import androidx.room.*
import com.tstreet.onhand.core.database.model.ShoppingListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * from shopping_list")
    @Transaction
    fun getShoppingList(): Flow<List<ShoppingListEntity>>
    
    @Insert()
    suspend fun insertShoppingList(shoppingList: List<ShoppingListEntity>)

    @Query("SELECT 1 from shopping_list WHERE name = :name AND isPurchased = 1")
    suspend fun isShoppingListIngredientPurchased(name: String): Boolean

    @Query("UPDATE shopping_list SET isPurchased = 1 WHERE name = :name AND isPurchased = 0")
    suspend fun markIngredientPurchased(name: String)

    @Query("UPDATE shopping_list SET isPurchased = 0 WHERE name = :name AND isPurchased = 1")
    fun unmarkIngredientPurchased(name: String)

    @Query("SELECT (SELECT COUNT(*) FROM shopping_list) == 0")
    suspend fun isEmpty(): Boolean

    @Query("DELETE from shopping_list")
    suspend fun clear()
}
