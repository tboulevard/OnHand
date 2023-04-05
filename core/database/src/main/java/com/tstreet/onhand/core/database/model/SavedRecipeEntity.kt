package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.RecipeDetail

@Entity(
    tableName = "saved_recipes"
)
data class SavedRecipeEntity(
    @PrimaryKey val id : Int,
    @ColumnInfo(name = "sourceUrl") val sourceUrl: String,
)

fun SavedRecipeEntity.asExternalModel() = RecipeDetail(
    id = id,
    sourceUrl = sourceUrl
)

fun RecipeDetail.toEntity() = SavedRecipeEntity(
    id = id,
    sourceUrl = sourceUrl
)
