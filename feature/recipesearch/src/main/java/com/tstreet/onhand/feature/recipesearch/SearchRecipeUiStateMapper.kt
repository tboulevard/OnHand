package com.tstreet.onhand.feature.recipesearch

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.data.RecipePreviewWithSaveState
import com.tstreet.onhand.core.model.domain.RecipeSearchResult
import com.tstreet.onhand.core.model.ui.IngredientAvailability
import com.tstreet.onhand.core.model.ui.RecipeSaveState
import com.tstreet.onhand.core.model.ui.RecipeSearchUiState
import com.tstreet.onhand.core.model.ui.RecipeWithSaveState
import com.tstreet.onhand.core.model.data.Ingredient
import javax.inject.Inject

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

// TODO: duplicated, centralize later
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
            // TODO: refactor, weak cohesion
            ingredientShoppingCartState = mutableStateOf(
                when {
                    it.preview.missedIngredients.containsAllIngredients(it.ingredientsMissingButInShoppingList) -> {
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

/**
 * Extension function that checks if all ingredients in the current list are contained in another list.
 * Ingredients are compared by their ids.
 */
private fun List<Ingredient>.containsAllIngredients(ingredients: List<Ingredient>): Boolean {
    val thisIds = this.map { it.id }.toSet()
    val otherIds = ingredients.map { it.id }.toSet()
    return otherIds.containsAll(thisIds)
}
