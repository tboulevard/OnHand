package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.ShoppingListIngredient

@Entity(
    tableName = "shopping_list"
)
data class ShoppingListEntity(
    @PrimaryKey val ingredientId: Int,
    @ColumnInfo val ingredient: Ingredient,
    @ColumnInfo val mappedRecipePreview: RecipePreview
)

fun ShoppingListEntity.toExternalModel(): ShoppingListIngredient {
    return ShoppingListIngredient(
        ingredient = ingredient,
        mappedRecipePreview = mappedRecipePreview
    )
}

fun ShoppingListIngredient.asEntity(): ShoppingListEntity {
    return ShoppingListEntity(
        ingredientId = ingredient.id,
        ingredient = ingredient,
        mappedRecipePreview = mappedRecipePreview!!
    )
}
