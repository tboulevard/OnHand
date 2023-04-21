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

    @Query("SELECT 1 from shopping_list WHERE id = :id AND isPurchased = 1")
    suspend fun isShoppingListIngredientPurchased(id: Int): Boolean

    @Query("UPDATE shopping_list SET isPurchased = 1 WHERE id = :id AND isPurchased = 0")
    suspend fun markIngredientPurchased(id: Int)

    @Query("UPDATE shopping_list SET isPurchased = 0 WHERE id = :id AND isPurchased = 1")
    fun unmarkIngredientPurchased(id: Int)

    @Query("SELECT (SELECT COUNT(*) FROM shopping_list) == 0")
    suspend fun isEmpty(): Boolean

    @Query("DELETE from shopping_list")
    suspend fun clear()
}
