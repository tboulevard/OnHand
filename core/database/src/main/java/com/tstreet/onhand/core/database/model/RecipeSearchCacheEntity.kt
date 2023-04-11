package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.RecipeIngredient

@Entity(
    tableName = "recipe_search_cache"
)
data class RecipeSearchCacheEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "imageType") val imageType: String,
    @ColumnInfo(name = "missedIngredients") val missedIngredients: List<RecipeIngredient>,
    @ColumnInfo(name = "missedIngredientCount") val missedIngredientCount: Int,
    @ColumnInfo(name = "usedIngredients") val usedIngredients: List<RecipeIngredient>,
    @ColumnInfo(name = "usedIngredientCount") val usedIngredientCount: Int,
    @ColumnInfo(name = "sourceUrl") val likes: Int
)

fun RecipeSearchCacheEntity.asExternalModel(): Recipe {
    return Recipe(
        id = id,
        title = title,
        image = image,
        imageType = imageType,
        missedIngredients = missedIngredients,
        missedIngredientCount = missedIngredientCount,
        usedIngredients = usedIngredients,
        usedIngredientCount = usedIngredientCount,
        likes = likes
    )
}

fun Recipe.toEntity(): RecipeSearchCacheEntity {
    return RecipeSearchCacheEntity(
        id = id,
        title = title,
        image = image,
        imageType = imageType,
        missedIngredients = missedIngredients,
        missedIngredientCount = missedIngredientCount,
        usedIngredients = usedIngredients,
        usedIngredientCount = usedIngredientCount,
        likes = likes
    )
}
