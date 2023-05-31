package com.tstreet.onhand.core.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.tstreet.onhand.core.model.SaveableRecipe

sealed interface RecipeSearchUiState {

    object Loading : RecipeSearchUiState

    data class Success(
        val recipes: SnapshotStateList<RecipeWithSaveState>,
    ) : RecipeSearchUiState

    data class Error(
        val recipes: SnapshotStateList<RecipeWithSaveState>
    ) : RecipeSearchUiState
}

enum class RecipeSaveState {
    SAVED,
    NOT_SAVED,
    SAVING
}

fun List<SaveableRecipe>?.toRecipeWithSaveStateItemList(): SnapshotStateList<RecipeWithSaveState> {
    return this?.map {
        RecipeWithSaveState(
            recipePreview = it.recipePreview,
            recipeSaveState =
            when {
                it.isSaved -> {
                    RecipeSaveState.SAVED
                }
                else -> {
                    RecipeSaveState.NOT_SAVED
                }
            }
        )
    }?.toMutableStateList() ?: mutableStateListOf()
}
