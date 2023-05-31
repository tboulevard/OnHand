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
    @Embedded val previewProperties: RecipePreviewProperties
)

fun RecipeSearchCacheEntity.asRecipePreview(): Recipe {
    return Recipe(
        id = id,
        title = previewProperties.title,
        image = previewProperties.image,
        imageType = previewProperties.imageType,
        missedIngredients = previewProperties.missedIngredients,
        missedIngredientCount = previewProperties.missedIngredientCount,
        usedIngredients = previewProperties.usedIngredients,
        usedIngredientCount = previewProperties.usedIngredientCount,
        likes = previewProperties.likes,
        // All search cache entities are sourced from API for now, so never custom.
        isCustom = false
    )
}

fun Recipe.toSearchCacheEntity(): RecipeSearchCacheEntity {
    return RecipeSearchCacheEntity(
        id = id,
        RecipePreviewProperties(
            title = title,
            image = image,
            imageType = imageType,
            missedIngredients = missedIngredients,
            missedIngredientCount = missedIngredientCount,
            usedIngredients = usedIngredients,
            usedIngredientCount = usedIngredientCount,
            likes = likes
        )
        // NOTE: we do not save RecipeDetailProperties because we rely on API providing this info
        //  ( and we currently do not cache the info from API for this)
    )
}
