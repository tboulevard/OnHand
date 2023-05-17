package com.tstreet.onhand.core.database.dao

import androidx.room.*
import com.tstreet.onhand.core.database.model.SavedRecipeEntity
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
    open suspend fun updateRecipesMissingIngredient(ingredientName: String) {
        // Get recipes where this ingredient is in missing ingredients list
        val recipesWithMissingIngredient = getRecipesContainingIngredient(ingredientName)
        // Map all recipes to new recipes where we copy all fields, changing missing/used
        // ingredients as needed.
        val newRecipes = recipesWithMissingIngredient.map {
            val missedIngredient = it.missedIngredients.find { it.ingredient.name == ingredientName }
            // Create a new list of missing ingredients that filters the given ingredient
            val newMissedIngredients =
                it.missedIngredients.filterNot { it.ingredient.name == ingredientName }
            val newUsedIngredients = it.usedIngredients + missedIngredient!! // TODO: handle null
            it.copy(
                missedIngredients = newMissedIngredients,
                missedIngredientCount = newMissedIngredients.size,
                usedIngredients = newUsedIngredients,
                usedIngredientCount = newUsedIngredients.size
            )
        }
        newRecipes.forEach {
            addRecipe(it)
        }
    }

    @Transaction
    open suspend fun updateRecipesUsingIngredient(ingredientName: String) {
        // Get recipes where this ingredient is in missing ingredients list
        val recipesUsingIngredient = getRecipesContainingIngredient(ingredientName)
        // Map all recipes to new recipes where we copy all fields, changing missing/used
        // ingredients as needed.
        val newRecipes = recipesUsingIngredient.map {
            val usedIngredient = it.usedIngredients.find { it.ingredient.name == ingredientName }
            // Create a new list of missing ingredients that filters the given ingredient
            val newUsedIngredients =
                it.usedIngredients.filterNot { it.ingredient.name == ingredientName }
            val newMissedIngredients = it.missedIngredients + usedIngredient!! // TODO: handle null
            it.copy(
                missedIngredients = newMissedIngredients,
                missedIngredientCount = newMissedIngredients.size,
                usedIngredients = newUsedIngredients,
                usedIngredientCount = newUsedIngredients.size
            )
        }
        newRecipes.forEach {
            addRecipe(it)
        }
    }

    @Query(
        """
        SELECT * FROM saved_recipes 
        WHERE (missedIngredients LIKE '%' || :ingredientName || '%') 
            OR (usedIngredients LIKE '%' || :ingredientName || '%') 
            """
    )
    abstract suspend fun getRecipesContainingIngredient(ingredientName: String): List<SavedRecipeEntity>
}
