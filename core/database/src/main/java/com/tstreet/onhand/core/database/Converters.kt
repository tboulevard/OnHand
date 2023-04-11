package com.tstreet.onhand.core.database

import androidx.room.TypeConverter
import com.tstreet.onhand.core.model.RecipeIngredient
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class Converters {

    // TODO: Revisit - storing as a string may not be best option here
    @TypeConverter
    fun toIngredientList(str: String): List<RecipeIngredient> {
        return Json.decodeFromString(str)
    }

    @TypeConverter
    fun fromIngredientList(ingredients: List<RecipeIngredient>): String {
        return Json.encodeToString(ingredients)
    }
}