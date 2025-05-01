package com.tstreet.onhand.core.model.ui

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState

sealed interface RecipeSearchUiState {

    data class Content(
        val recipes: List<RecipeWithSaveState>,
    ) : RecipeSearchUiState

    object Empty : RecipeSearchUiState

    object Error : RecipeSearchUiState

    object Loading : RecipeSearchUiState
}

fun List<RecipePreviewWithSaveState>.toRecipeWithSaveStateItemList(): List<RecipeWithSaveState> {
    return map {
        RecipeWithSaveState(
            preview = it.preview,
            saveState =
                mutableStateOf(
                    when {
                        it.isSaved -> {
                            RecipeSaveState.SAVED
                        }

                        else -> {
                            RecipeSaveState.NOT_SAVED
                        }
                    }
                )
        )
    }
}
