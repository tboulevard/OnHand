package com.tstreet.onhand.core.database.dao;

import androidx.room.*
import com.tstreet.onhand.core.database.model.ShoppingListEntity
import com.tstreet.onhand.core.model.RecipePreview

@Dao
interface ShoppingListDao {

    @Query("SELECT * from shopping_list")
    suspend fun getShoppingList(): List<ShoppingListEntity>

    @Query("SELECT DISTINCT mappedRecipePreview from shopping_list WHERE mappedRecipePreview IS NOT NULL")
    @Transaction
    suspend fun getRecipesInShoppingList(): List<RecipePreview?>

    // TODO: For now, we only allow a given ingredient to be mapped to one recipe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(shoppingList: List<ShoppingListEntity>)

    @Query("SELECT (SELECT COUNT(*) FROM shopping_list) == 0")
    suspend fun isEmpty(): Boolean

    @Query("DELETE from shopping_list WHERE mappedRecipePreview = :recipe")
    suspend fun removeRecipePreview(recipe: RecipePreview)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecipePreview(shoppingListEntity: ShoppingListEntity)

    @Query("DELETE FROM shopping_list WHERE ingredientId = :id")
    suspend fun removeIngredient(id: Int)
}
