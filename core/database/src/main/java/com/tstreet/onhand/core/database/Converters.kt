package com.tstreet.onhand.core.database

import androidx.room.TypeConverter
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.ByteArrayOutputStream

// https://developer.android.com/training/data-storage/room/referencing-data

class Converters {

    @TypeConverter
    fun toIngredient(str: String): Ingredient {
        return Json.decodeFromString(str)
    }

    @TypeConverter
    fun fromIngredient(ingredient: Ingredient): String {
        return Json.encodeToString(Ingredient)
    }

    @TypeConverter
    fun toIngredientList(str: String): List<Ingredient> {
        return Json.decodeFromString(str)
    }

    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>): String {
        return Json.encodeToString(ingredients)
    }
}