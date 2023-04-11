package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.CompositeRecipe
import com.tstreet.onhand.core.model.RecipeDetail
import com.tstreet.onhand.core.model.RecipeIngredient

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
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "sourceUrl") val sourceUrl: String,
)

fun SavedRecipeEntity.asExternalModel() = CompositeRecipe(
    id = id,
    title = title,
    image = image,
    imageType = imageType,
    missedIngredients = missedIngredients,
    missedIngredientCount = missedIngredientCount,
    usedIngredients = usedIngredients,
    usedIngredientCount = usedIngredientCount,
    likes = likes,
    sourceUrl = sourceUrl
)

fun CompositeRecipe.toEntity() = SavedRecipeEntity(
    id = id,
    title = title,
    image = image,
    imageType = imageType,
    missedIngredients = missedIngredients,
    missedIngredientCount = missedIngredientCount,
    usedIngredients = usedIngredients,
    usedIngredientCount = usedIngredientCount,
    likes = likes,
    sourceUrl = sourceUrl
)

//TODO: cleanup, just to get mvp working
fun SavedRecipeEntity.toRecipeDetail() = RecipeDetail(
    id = id,
    sourceUrl = sourceUrl
)
