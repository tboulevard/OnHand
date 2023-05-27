package com.tstreet.onhand.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.Recipe

@Entity(
    tableName = "recipe_search_cache"
)
data class RecipeSearchCacheEntity(
    @PrimaryKey val id: Int,
    @Embedded val storedRecipeProperties: StoredRecipeProperties
)

fun RecipeSearchCacheEntity.asExternalModel(): Recipe {
    return Recipe(
        id = id,
        title = storedRecipeProperties.title,
        image = storedRecipeProperties.image,
        imageType = storedRecipeProperties.imageType,
        missedIngredients = storedRecipeProperties.missedIngredients,
        missedIngredientCount = storedRecipeProperties.missedIngredientCount,
        usedIngredients = storedRecipeProperties.usedIngredients,
        usedIngredientCount = storedRecipeProperties.usedIngredientCount,
        likes = storedRecipeProperties.likes
    )
}

fun Recipe.toSearchCacheEntity(): RecipeSearchCacheEntity {
    return RecipeSearchCacheEntity(
        id = id,
        StoredRecipeProperties(
            title = title,
            image = image,
            imageType = imageType,
            missedIngredients = missedIngredients,
            missedIngredientCount = missedIngredientCount,
            usedIngredients = usedIngredients,
            usedIngredientCount = usedIngredientCount,
            likes = likes
        )
    )
}
