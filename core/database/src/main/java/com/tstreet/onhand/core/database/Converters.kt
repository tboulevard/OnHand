package com.tstreet.onhand.core.database

import androidx.room.TypeConverter
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class Converters {

    // TODO: Revisit - storing as a string may not be best option here
    @TypeConverter
    fun toIngredientList(str: String): List<Ingredient> {
        val decoded: List<Ingredient> = Json.decodeFromString(str)
        return decoded
    }

    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>): String {
        val encoded = Json.encodeToString(ingredients)
        return encoded
    }
}