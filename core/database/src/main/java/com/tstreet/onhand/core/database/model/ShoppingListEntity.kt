package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.ShoppingListIngredient

@Entity(
    tableName = "shopping_list"
)
data class ShoppingListEntity(
    @PrimaryKey val ingredientName: String,
    @ColumnInfo val mappedRecipePreview: RecipePreview,
    @ColumnInfo val isPurchased: Boolean
)

fun ShoppingListEntity.toExternalModel(): ShoppingListIngredient {
    return ShoppingListIngredient(
        name = ingredientName,
        mappedRecipePreview = mappedRecipePreview,
        isPurchased = isPurchased
    )
}

fun ShoppingListIngredient.asEntity(): ShoppingListEntity {
    return ShoppingListEntity(
        ingredientName = name,
        mappedRecipePreview = mappedRecipePreview!!,
        isPurchased = isPurchased
    )
}
