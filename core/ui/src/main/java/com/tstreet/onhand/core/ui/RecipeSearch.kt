package com.tstreet.onhand.core.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.SaveableRecipe

sealed interface RecipeSearchUiState {

    object Loading : RecipeSearchUiState

    data class Success(
        val recipes: SnapshotStateList<RecipeSearchItem>,
    ) : RecipeSearchUiState

    data class Error(
        val message: String
    ) : RecipeSearchUiState
}

enum class RecipeSaveState {
    SAVED,
    NOT_SAVED,
    SAVING
}

data class RecipeSearchItem(
    val recipe: SaveableRecipe,
    val recipeSaveState: RecipeSaveState
)

fun List<SaveableRecipe>.toRecipeSearchItemList(): List<RecipeSearchItem> {
    return this.map {
        RecipeSearchItem(
            recipe = it,
            recipeSaveState =
            when (it.isSaved) {
                true -> {
                    RecipeSaveState.SAVED
                }
                else -> {
                    RecipeSaveState.NOT_SAVED
                }
            }
        )
    }
}