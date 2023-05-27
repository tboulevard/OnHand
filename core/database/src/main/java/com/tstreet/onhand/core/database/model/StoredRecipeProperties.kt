package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import com.tstreet.onhand.core.model.RecipeIngredient

data class StoredRecipeProperties(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "imageType") val imageType: String,
    @ColumnInfo(name = "missedIngredients") val missedIngredients: List<RecipeIngredient>,
    @ColumnInfo(name = "missedIngredientCount") val missedIngredientCount: Int,
    @ColumnInfo(name = "usedIngredients") val usedIngredients: List<RecipeIngredient>,
    @ColumnInfo(name = "usedIngredientCount") val usedIngredientCount: Int,
    @ColumnInfo(name = "instructions") val instructions: String? = null,
    @ColumnInfo(name = "likes") val likes: Int
)