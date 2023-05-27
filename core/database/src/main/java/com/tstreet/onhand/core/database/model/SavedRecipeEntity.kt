package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.*

@Entity(
    tableName = "saved_recipes"
)
data class SavedRecipeEntity(
    @PrimaryKey val id: Int,
    // Using composition to share fields across entities
    @Embedded val recipeProperties: StoredRecipeProperties,
    // Entity specific fields can be placed here
    @ColumnInfo(name = "isCustomRecipe") val isCustomRecipe: Boolean = false
)

fun SavedRecipeEntity.asExternalModel() =
    SaveableRecipe(
        Recipe(
            id = id,
            title = recipeProperties.title,
            image = recipeProperties.image,
            imageType = recipeProperties.imageType,
            missedIngredients = recipeProperties.missedIngredients,
            missedIngredientCount = recipeProperties.missedIngredientCount,
            usedIngredients = recipeProperties.usedIngredients,
            usedIngredientCount = recipeProperties.usedIngredientCount,
            instructions = recipeProperties.instructions,
            likes = recipeProperties.likes,
        ),
        isSaved = true, // TODO: refactor, for now assume true - all recipes in this table are saved
        isCustom = isCustomRecipe
    )

fun Recipe.toSavedRecipeEntity(isCustomRecipe: Boolean) =
    SavedRecipeEntity(
        id = id,
        StoredRecipeProperties(
            title = title,
            image = image,
            imageType = imageType,
            missedIngredients = missedIngredients,
            missedIngredientCount = missedIngredientCount,
            usedIngredients = usedIngredients,
            usedIngredientCount = usedIngredientCount,
            instructions = instructions,
            likes = likes,
        ),
        isCustomRecipe = isCustomRecipe
    )
