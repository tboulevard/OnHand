package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.*

@Entity(
    tableName = "saved_recipes"
)
data class SavedRecipeEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "imageType") val imageType: String,
    @ColumnInfo(name = "missedIngredients") val missedIngredients: List<RecipeIngredient>,
    @ColumnInfo(name = "missedIngredientCount") val missedIngredientCount: Int,
    @ColumnInfo(name = "usedIngredients") val usedIngredients: List<RecipeIngredient>,
    @ColumnInfo(name = "usedIngredientCount") val usedIngredientCount: Int,
    @ColumnInfo(name = "instructions") val instructions: String,
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "isCustomRecipe") val isCustomRecipe: Boolean = false
)

fun SavedRecipeEntity.asExternalModel() =
    SaveableRecipe(
        Recipe(
            id = id,
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
        isSaved = true, // TODO: refactor, for now assume true - all recipes in this table are saved
        isCustom = isCustomRecipe
    )

fun SavedRecipeEntity.asViewableRecipe() =
    RecipeDetail(
        id = id,
        title = title,
        instructions = instructions,
        summary = "TODO",
    )

fun Recipe.toSavedRecipeEntity(isCustomRecipe: Boolean) = SavedRecipeEntity(
    id = id,
    title = title,
    image = image,
    imageType = imageType,
    missedIngredients = missedIngredients,
    missedIngredientCount = missedIngredientCount,
    usedIngredients = usedIngredients,
    usedIngredientCount = usedIngredientCount,
    instructions = instructions,
    likes = likes,
    isCustomRecipe = isCustomRecipe
)
