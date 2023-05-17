package com.tstreet.onhand.core.database.dao

import androidx.room.*
import com.tstreet.onhand.core.database.model.SavedRecipeEntity
import com.tstreet.onhand.core.model.RecipeIngredient
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SavedRecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addRecipe(recipe: SavedRecipeEntity)

    @Query("SELECT 1 from saved_recipes WHERE id = :id")
    abstract suspend fun isRecipeSaved(id: Int): Int

    @Query("DELETE FROM saved_recipes WHERE id = :id")
    abstract suspend fun deleteRecipe(id: Int)

    @Query("SELECT * from saved_recipes WHERE id = :id")
    @Transaction
    abstract fun getRecipe(id: Int): Flow<SavedRecipeEntity>

    @Query("SELECT * from saved_recipes")
    @Transaction
    abstract fun getSavedRecipes(): Flow<List<SavedRecipeEntity>>

    @Transaction
    open suspend fun updateRecipesForIngredient(ingredientName: String) {
        val recipesWithMissingIngredient = getRecipesContainingIngredient(ingredientName)
        val newRecipes = recipesWithMissingIngredient.map {
            val newMissedIngredients =
                it.missedIngredients.filterNot { it.ingredient.name == ingredientName }
            val newMissedIngredientCount = newMissedIngredients.size
            it.copy(
                missedIngredients = newMissedIngredients,
                missedIngredientCount = newMissedIngredientCount
            )
        }
        println("[OnHand] Recipes containing ingredient= $newRecipes")
        newRecipes.forEach {
            println("[OnHand] Replacing recipe with these contents=${it.missedIngredients}")
            deleteRecipe(it.id)
            addRecipe(it)
        }
    }

    @Query("SELECT * FROM saved_recipes WHERE missedIngredients LIKE '%' || :ingredientName || '%'")
    abstract suspend fun getRecipesContainingIngredient(ingredientName: String): List<SavedRecipeEntity>
}
