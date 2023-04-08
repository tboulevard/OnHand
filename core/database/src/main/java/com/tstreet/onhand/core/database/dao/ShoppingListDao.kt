package com.tstreet.onhand.core.database.dao;

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.Query;
import com.tstreet.onhand.core.database.model.ShoppingListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Insert
    suspend fun insertShoppingList(shoppingList: List<ShoppingListEntity>)

    @Query("SELECT * from shopping_list")
    fun getShoppingList(): Flow<List<ShoppingListEntity>>

    @Query("DELETE from shopping_list")
    suspend fun clear()
}
