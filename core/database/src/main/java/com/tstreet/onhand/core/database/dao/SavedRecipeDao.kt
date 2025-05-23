package com.tstreet.onhand.core.database.dao

import androidx.room.*
import com.tstreet.onhand.core.database.model.SavedRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SavedRecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun replaceRecipe(recipe: SavedRecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addRecipe(recipe: SavedRecipeEntity)

    @Query("SELECT 1 from saved_recipes WHERE id = :id")
    abstract suspend fun isRecipeSaved(id: Int): Int

    @Query("SELECT 1 from saved_recipes WHERE title = :title")
    abstract suspend fun isRecipeSaved(title: String): Int

    @Query("DELETE FROM saved_recipes WHERE id = :id")
    abstract suspend fun deleteRecipe(id: Int)

    @Query("SELECT * from saved_recipes WHERE id = :id")
    abstract suspend fun getRecipe(id: Int): SavedRecipeEntity

    @Query("SELECT * from saved_recipes")
    @Transaction
    abstract fun getAll(): Flow<List<SavedRecipeEntity>>

    @Query("SELECT 1 from saved_recipes WHERE id = :id AND isCustomRecipe = 1")
    abstract suspend fun isRecipeCustom(id: Int): Int

    @Transaction
    open suspend fun updateRecipesMissingIngredient(ingredientName: String) {
        getRecipesMissingIngredient(ingredientName).map { entity ->
            entity.previewProperties.missedIngredients.find { it.name == ingredientName }
                ?.let { missedIngredient ->
                    val newMissedIngredients =
                        entity.previewProperties.missedIngredients.filterNot { it.name == ingredientName }
                    val newUsedIngredients =
                        entity.previewProperties.usedIngredients + missedIngredient
                    // Copy all existing recipe properties except those that we want changed
                    val newProperties = entity.previewProperties.copy(
                        missedIngredients = newMissedIngredients,
                        missedIngredientCount = newMissedIngredients.size,
                        usedIngredients = newUsedIngredients,
                        usedIngredientCount = newUsedIngredients.size
                    )
                    // Create a new SavedRecipeEntity with new recipe properties
                    entity.copy(
                        previewProperties = newProperties
                    )
                }
        }.forEach { updatedEntity ->
            // If no entities are updated, no recipes are changed
            updatedEntity?.apply {
                replaceRecipe(this)
            }
        }
    }

    @Transaction
    open suspend fun updateRecipesUsingIngredient(ingredientName: String) {
        getRecipesUsingIngredient(ingredientName).map { entity ->
            entity.previewProperties.usedIngredients.find { it.name == ingredientName }
                ?.let { usedIngredient ->
                    val newUsedIngredients =
                        entity.previewProperties.usedIngredients.filterNot { it.name == ingredientName }
                    val newMissedIngredients =
                        entity.previewProperties.missedIngredients + usedIngredient
                    val newProperties = entity.previewProperties.copy(
                        missedIngredients = newMissedIngredients,
                        missedIngredientCount = newMissedIngredients.size,
                        usedIngredients = newUsedIngredients,
                        usedIngredientCount = newUsedIngredients.size
                    )
                    entity.copy(
                        previewProperties = newProperties
                    )
                }
        }.forEach { updatedEntity ->
            // If no entities are updated, no recipes are changed
            updatedEntity?.apply {
                replaceRecipe(this)
            }
        }
    }

    @Query(
        """
        SELECT * FROM saved_recipes 
        WHERE missedIngredients LIKE '%' || :ingredientName || '%'
            """
    )
    abstract suspend fun getRecipesMissingIngredient(
        ingredientName: String
    ): List<SavedRecipeEntity>

    @Query(
        """
        SELECT * FROM saved_recipes 
        WHERE usedIngredients LIKE '%' || :ingredientName || '%'
            """
    )
    abstract suspend fun getRecipesUsingIngredient(
        ingredientName: String
    ): List<SavedRecipeEntity>
}
