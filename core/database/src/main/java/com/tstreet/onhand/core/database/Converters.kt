package com.tstreet.onhand.core.database

import androidx.room.TypeConverter
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.Ingredient
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class Converters {

    // TODO: Revisit - storing as a string may not be best option here
    @TypeConverter
    fun toIngredientList(str: String): List<Ingredient> {
        return Json.decodeFromString(str)
    }

    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>): String {
        return Json.encodeToString(ingredients)
    }

    @TypeConverter
    fun toRecipePreview(str: String?): RecipePreview? {
        return if (str != null) {
            Json.decodeFromString(str)
        } else {
            null
        }
    }

    @TypeConverter
    fun fromRecipePreview(recipePreview: RecipePreview?): String? {
        return if (recipePreview != null) {
            Json.encodeToString(recipePreview)
        } else {
            null
        }
    }

    @TypeConverter
    fun toIngredient(str: String?): Ingredient? {
        return if (str != null) {
            Json.decodeFromString(str)
        } else {
            null
        }
    }

    @TypeConverter
    fun fromIngredient(ingredient: Ingredient?): String? {
        return if (ingredient != null) {
            Json.encodeToString(ingredient)
        } else {
            null
        }
    }
}
