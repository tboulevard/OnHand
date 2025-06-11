package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.data.PantryIngredient

@Entity(
    tableName = "pantry"
)
class PantryEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "ingredientName") val ingredientName: String,
    @ColumnInfo(name = "category") val category: IngredientCategory
)

fun PantryIngredient.toPantryEntity() =
    PantryEntity(
        id = ingredient.id,
        ingredientName = ingredient.name,
        category = IngredientCategory.randomCategory()
    )

fun Ingredient.toPantryEntity() =
    PantryEntity(
        id = id,
        ingredientName = name,
        category = IngredientCategory.randomCategory()
    )

fun PantryEntity.toPantryIngredient() =
    PantryIngredient(
        ingredient = Ingredient(
            id = id,
            name = ingredientName,
            category = category
        ),
        inPantry = true
    )
