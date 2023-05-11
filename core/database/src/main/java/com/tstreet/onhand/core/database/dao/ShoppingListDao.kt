package com.tstreet.onhand.core.database.dao;

import androidx.room.*
import com.tstreet.onhand.core.database.model.ShoppingListEntity
import com.tstreet.onhand.core.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * from shopping_list")
    suspend fun getShoppingList(): List<ShoppingListEntity>

    @Query("SELECT DISTINCT mappedRecipe from shopping_list WHERE mappedRecipe IS NOT NULL")
    suspend fun getRecipesInShoppingList(): List<Recipe?>

    // TODO: For now, we only allow a given ingredient to be mapped to one recipe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(shoppingList: List<ShoppingListEntity>)

    @Query("SELECT 1 from shopping_list WHERE ingredientName = :name AND isPurchased = 1")
    suspend fun isShoppingListIngredientPurchased(name: String): Boolean

    @Query("UPDATE shopping_list SET isPurchased = 1 WHERE ingredientName = :name AND isPurchased = 0")
    suspend fun markIngredientPurchased(name: String)

    @Query("UPDATE shopping_list SET isPurchased = 0 WHERE ingredientName = :name AND isPurchased = 1")
    fun unmarkIngredientPurchased(name: String)

    @Query("SELECT (SELECT COUNT(*) FROM shopping_list) == 0")
    suspend fun isEmpty(): Boolean

    @Query("DELETE from shopping_list")
    suspend fun clear()
}
