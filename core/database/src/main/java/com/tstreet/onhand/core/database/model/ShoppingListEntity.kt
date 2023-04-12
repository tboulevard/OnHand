package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient

@Entity(
    tableName = "shopping_list"
)
data class ShoppingListEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val amount: () -> String,
    @ColumnInfo val unit: () -> String,
    // TODO: Make it a list a mapped recipes as ingredients can be part of multiple
    @ColumnInfo val mappedRecipes: () -> List<Recipe>
)

fun ShoppingListEntity.toExternalModel(): ShoppingListIngredient {
    return ShoppingListIngredient(
        id = id,
        name = name,
        amount = amount,
        unit = unit,
        mappedRecipes = mappedRecipes
    )
}

fun ShoppingListIngredient.asEntity(): ShoppingListEntity {
    return ShoppingListEntity(
        id = id,
        name = name,
        amount = amount,
        unit = unit,
        mappedRecipes = mappedRecipes
    )
}
