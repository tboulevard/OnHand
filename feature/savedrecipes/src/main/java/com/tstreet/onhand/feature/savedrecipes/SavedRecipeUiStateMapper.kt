package com.tstreet.onhand.feature.savedrecipes

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState
import com.tstreet.onhand.core.model.domain.SavedRecipesResult
import com.tstreet.onhand.core.model.ui.IngredientAvailability
import com.tstreet.onhand.core.model.ui.RecipeSaveState
import com.tstreet.onhand.core.model.ui.RecipeWithSaveState
import com.tstreet.onhand.core.model.ui.SavedRecipesUiState
import javax.inject.Inject

@FeatureScope
class SavedRecipeUiStateMapper @Inject constructor() {

    fun mapSavedRecipesResultToUi(result: SavedRecipesResult): SavedRecipesUiState {
        return when (result) {
            is SavedRecipesResult.Success -> {
                if (result.recipes.isEmpty()) {
                    SavedRecipesUiState.Empty
                } else {
                    SavedRecipesUiState.Content(recipes = result.recipes.toRecipeWithSaveStateItemList())
                }
            }

            is SavedRecipesResult.Error -> SavedRecipesUiState.Error
            is SavedRecipesResult.Loading -> SavedRecipesUiState.Loading
        }
    }
}

// TODO: duplicated - centralize later
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
                ),
            ingredientState = mutableStateOf(
                when {
                    it.preview.missedIngredientCount > 0 -> {
                        IngredientAvailability.MISSING_INGREDIENTS
                    }

                    else -> {
                        IngredientAvailability.ALL_INGREDIENTS_AVAILABLE
                    }
                }
            ),
            // TODO: reflect missing ingredient state in real time
            missingIngredientsInCart = mutableStateOf(it.ingredientsMissingButInShoppingList)
        )
    }
}