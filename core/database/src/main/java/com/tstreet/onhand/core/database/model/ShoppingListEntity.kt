package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.ShoppingListIngredient

@Entity(
    tableName = "shopping_list"
)
data class ShoppingListEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val quantity: Int,
    @ColumnInfo val unitOfMeasure: String,
    //@ColumnInfo val mappedRecipes: List<Recipe>
)

fun ShoppingListEntity.toExternalModel(): ShoppingListIngredient {
    return ShoppingListIngredient(
        id = id,
        name = name,
        // TODO: Revisit, quantity/unit of measure later
        quantity = quantity,
        unitOfMeasure = unitOfMeasure,
        //mappedRecipes  = mappedRecipes
    )
}

fun ShoppingListIngredient.asEntity(): ShoppingListEntity {
    return ShoppingListEntity(
        id = id,
        name = name,
        // TODO: Revisit, quantity/unit of measure later
        quantity = quantity,
        unitOfMeasure = unitOfMeasure,
        //mappedRecipes  = mappedRecipes
    )
}
