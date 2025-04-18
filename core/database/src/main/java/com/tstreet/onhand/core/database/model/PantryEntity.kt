package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.Ingredient

@Entity(
    tableName = "pantry"
)
class PantryEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "ingredientName") val ingredientName: String
)

fun Ingredient.toPantryEntity() =
    PantryEntity(
        id = id,
        ingredientName = name
    )

fun PantryEntity.toIngredient() =
        Ingredient(
            id = id,
            name = ingredientName
        )