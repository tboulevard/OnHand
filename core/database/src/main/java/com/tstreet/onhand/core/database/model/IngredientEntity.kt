package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.SearchIngredient

@Entity(
    tableName = "ingredient_catalog"
)
class IngredientEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val ingredientName: String,
    // OBSOLETE, just here to DB import works
    @ColumnInfo(name = "inPantry") val inPantry: Boolean
)

fun IngredientEntity.toIngredient() =
    Ingredient(
        id = id,
        name = ingredientName
    )

fun IngredientEntity.toSearchIngredient() =
    SearchIngredient(
        id = id,
        name = ingredientName
    )

fun Ingredient.toIngredientEntity() =
    IngredientEntity(
        id = id,
        ingredientName = name,
        inPantry = false
    )