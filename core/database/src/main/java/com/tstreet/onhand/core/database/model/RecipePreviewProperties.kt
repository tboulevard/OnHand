package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import com.tstreet.onhand.core.model.data.Ingredient

/**
 * Minimum set of information expected for a saved Recipe, regardless of if sourced from user or
 * API. Essentially set if info to display on recipe cards for previewing.
 */
data class RecipePreviewProperties(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "imageType") val imageType: String,
    // TODO: potentially pull this into detail model
    @ColumnInfo(name = "missedIngredients") val missedIngredients: List<Ingredient>,
    @ColumnInfo(name = "missedIngredientCount") val missedIngredientCount: Int,
    // TODO: potentially pull this into detail model
    @ColumnInfo(name = "usedIngredients") val usedIngredients: List<Ingredient>,
    @ColumnInfo(name = "usedIngredientCount") val usedIngredientCount: Int,
    @ColumnInfo(name = "likes") val likes: Int
)
