package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo

/**
 * Additional information about the recipe (only seen when recipe is navigated to currently).
 *
 * For now we only save this info in the DB for custom recipes. Non-custom recipes source this
 * information from API.
 */
data class RecipeDetailProperties(
    // Optional fields
    @ColumnInfo(name = "instructions") val instructions: String? = null,
)