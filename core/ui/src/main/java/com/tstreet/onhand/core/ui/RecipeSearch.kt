package com.tstreet.onhand.core.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
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
    val saveableRecipe: SaveableRecipe,
    val recipeSaveState: RecipeSaveState
)

fun List<SaveableRecipe>.toRecipeSearchItemList(): SnapshotStateList<RecipeSearchItem> {
    return this.map {
        RecipeSearchItem(
            saveableRecipe = it,
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
    }.toMutableStateList()
}
