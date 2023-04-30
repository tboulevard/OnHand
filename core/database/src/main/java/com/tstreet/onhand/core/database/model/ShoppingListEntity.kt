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
    @PrimaryKey val name: String,
    @ColumnInfo val mappedRecipes: List<Recipe>,
    @ColumnInfo val isPurchased: Boolean
)

fun ShoppingListEntity.toExternalModel(): ShoppingListIngredient {
    return ShoppingListIngredient(
        name = name,
        mappedRecipes = mappedRecipes,
        isPurchased = isPurchased
    )
}

fun ShoppingListIngredient.asEntity(): ShoppingListEntity {
    return ShoppingListEntity(
        name = name,
        mappedRecipes = mappedRecipes,
        isPurchased = isPurchased
    )
}
