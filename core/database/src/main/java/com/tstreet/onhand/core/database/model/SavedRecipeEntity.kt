package com.tstreet.onhand.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstreet.onhand.core.model.*

@Entity(
    tableName = "saved_recipes"
)
data class SavedRecipeEntity(
    @PrimaryKey val id: Int,
    // Using composition to share fields across entities
    @Embedded val previewProperties: RecipePreviewProperties,
    // NOTE: We allow this to be null as a whole, but if it's included we require certain fields.
    //  For now this is mainly to distinguish between custom and api src'd recipes
    @Embedded val detailProperties: RecipeDetailProperties? = null,
    // Entity specific fields can be placed here
    @ColumnInfo(name = "isCustomRecipe") val isCustomRecipe: Boolean = false
)

fun SavedRecipeEntity.asSaveableRecipePreview(): SaveableRecipe {
    return SaveableRecipe(
        RecipePreview(
            id = id,
            title = previewProperties.title,
            image = previewProperties.image,
            imageType = previewProperties.imageType,
            missedIngredients = previewProperties.missedIngredients,
            missedIngredientCount = previewProperties.missedIngredientCount,
            usedIngredients = previewProperties.usedIngredients,
            usedIngredientCount = previewProperties.usedIngredientCount,
            likes = previewProperties.likes,
            isCustom = isCustomRecipe,
        ),
        isSaved = true, // TODO: refactor, for now assume true - all recipes in this table are saved
    )
}

// For non custom recipes, we expect all recipes to src detail from API. So no point in saving it
// now. Could impl caching later though.
fun RecipePreview.toSavedRecipeEntity(): SavedRecipeEntity {
    return SavedRecipeEntity(
        id = id,
        RecipePreviewProperties(
            title = title,
            image = image,
            imageType = imageType,
            missedIngredients = missedIngredients,
            missedIngredientCount = missedIngredientCount,
            usedIngredients = usedIngredients,
            usedIngredientCount = usedIngredientCount,
            likes = likes,
        ),
        isCustomRecipe = false
    )
}

fun SavedRecipeEntity.asFullRecipe() =
    FullRecipe(
        preview = RecipePreview(
            id = id,
            title = previewProperties.title,
            image = previewProperties.image,
            imageType = previewProperties.imageType,
            usedIngredientCount = previewProperties.usedIngredientCount,
            usedIngredients = previewProperties.usedIngredients,
            missedIngredientCount = previewProperties.missedIngredientCount,
            missedIngredients = previewProperties.missedIngredients,
            likes = previewProperties.likes,
            isCustom = isCustomRecipe
        ),
        detail = RecipeDetail(
            instructions = detailProperties?.instructions
        )
    )


// We expect all detail info to be provided by user, so we save it with the recipe

fun createCustomSavedRecipeEntity(
    recipe: FullRecipe
): SavedRecipeEntity {
    return SavedRecipeEntity(
        id = recipe.preview.id,
        previewProperties = RecipePreviewProperties(
            title = recipe.preview.title,
            image = recipe.preview.image,
            imageType = recipe.preview.imageType,
            missedIngredients = recipe.preview.missedIngredients,
            missedIngredientCount = recipe.preview.missedIngredientCount,
            usedIngredients = recipe.preview.usedIngredients,
            usedIngredientCount = recipe.preview.usedIngredientCount,
            likes = recipe.preview.likes,
        ),
        detailProperties = RecipeDetailProperties(
            instructions = recipe.detail.instructions
        ),
        isCustomRecipe = recipe.preview.isCustom
    )
}
