package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.PantryIngredient

@Entity(
    tableName = "pantry"
)
class PantryEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "ingredientName") val ingredientName: String
)

fun PantryIngredient.toPantryEntity() =
    PantryEntity(
        id = ingredient.id,
        ingredientName = ingredient.name
    )

fun Ingredient.toPantryEntity() =
    PantryEntity(
        id = id,
        ingredientName = name
    )

fun PantryEntity.toPantryIngredient() =
    PantryIngredient(
        ingredient = Ingredient(
            id = id,
            name = ingredientName,
        ),
        inPantry = true
    )
