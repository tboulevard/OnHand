package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.Ingredient

// TODO: Think about reusing this entity for stuff in the pantry, just add `isSaved`
@Entity(
    tableName = "ingredient_catalog"
)
data class IngredientCatalogEntity(
    @PrimaryKey val id : Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "inPantry") val inPantry: Boolean
)

fun IngredientCatalogEntity.asExternalModel() = Ingredient(
    id = id,
    name = name,
    inPantry = inPantry
)
