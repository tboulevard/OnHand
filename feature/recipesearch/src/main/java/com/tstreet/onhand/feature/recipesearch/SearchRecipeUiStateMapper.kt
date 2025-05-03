package com.tstreet.onhand.feature.recipesearch

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState
import com.tstreet.onhand.core.model.domain.RecipeSearchResult
import com.tstreet.onhand.core.model.ui.IngredientAvailability
import com.tstreet.onhand.core.model.ui.RecipeSaveState
import com.tstreet.onhand.core.model.ui.RecipeSearchUiState
import com.tstreet.onhand.core.model.ui.RecipeWithSaveState
import javax.inject.Inject

/**
 * Extension function to check if the list is a subset of another list
 * Returns true if every element in this list is contained in the other list
 */
private fun <T> List<T>.isSubsetOf(other: List<T>): Boolean {
    if (this.isEmpty() && other.isNotEmpty()) return false

    return this.all { it in other }
}

@FeatureScope
class SearchRecipeUiStateMapper @Inject constructor() {

    fun mapGetRecipesResultToUi(result: RecipeSearchResult): RecipeSearchUiState {
        return when (result) {
            is RecipeSearchResult.Success -> {
                if (result.recipes.isEmpty()) {
                    RecipeSearchUiState.Empty
                } else {
                    RecipeSearchUiState.Content(recipes = result.recipes.toRecipeWithSaveStateItemList())
                }
            }

            is RecipeSearchResult.Error -> RecipeSearchUiState.Error
            is RecipeSearchResult.Loading -> RecipeSearchUiState.Loading
        }
    }
}

fun List<RecipePreviewWithSaveState>.toRecipeWithSaveStateItemList(): List<RecipeWithSaveState> {


    return map {

        val ingredientsMissingButInShoppingList = it.ingredientsMissingButInShoppingList
        val ingredientsMissing = it.preview.missedIngredients

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
            ingredientPantryState = mutableStateOf(
                when {
                    it.preview.missedIngredientCount > 0 -> {
                        IngredientAvailability.MISSING_INGREDIENTS
                    }

                    else -> {
                        IngredientAvailability.ALL_INGREDIENTS_AVAILABLE
                    }
                }
            ),
            ingredientShoppingCartState = mutableStateOf(
                when {
                    ingredientsMissingButInShoppingList.isSubsetOf(ingredientsMissing) -> {
                        IngredientAvailability.ALL_INGREDIENTS_AVAILABLE
                    }

                    else -> {
                        IngredientAvailability.MISSING_INGREDIENTS
                    }
                }
            )
        )
    }
}