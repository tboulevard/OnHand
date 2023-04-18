package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.RecipeMeasure
import com.tstreet.onhand.core.model.ShoppingListIngredient

@Entity(
    tableName = "shopping_list"
)
data class ShoppingListEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val recipeMeasures: List<RecipeMeasure>,
    @ColumnInfo val isPurchased: Boolean
)

fun ShoppingListEntity.toExternalModel(): ShoppingListIngredient {
    return ShoppingListIngredient(
        id = id,
        name = name,
        recipeMeasures = recipeMeasures,
        isPurchased = isPurchased
    )
}

fun ShoppingListIngredient.asEntity(): ShoppingListEntity {
    return ShoppingListEntity(
        id = id,
        name = name,
        recipeMeasures = recipeMeasures,
        isPurchased = isPurchased
    )
}
