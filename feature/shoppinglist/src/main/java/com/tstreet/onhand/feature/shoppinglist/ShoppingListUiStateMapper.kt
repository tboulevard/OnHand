package com.tstreet.onhand.feature.shoppinglist

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.ShoppingListResult
import com.tstreet.onhand.core.model.ui.ShoppingListUiState
import com.tstreet.onhand.core.model.ui.UiShoppingListIngredient
import com.tstreet.onhand.core.model.ui.UiShoppingListRecipe
import javax.inject.Inject

@FeatureScope
class ShoppingListUiStateMapper @Inject constructor() {

    fun mapShoppingListResultToUi(
        shoppingListResult: ShoppingListResult
    ): ShoppingListUiState = when (shoppingListResult) {
        is ShoppingListResult.Error -> {
            ShoppingListUiState.Error
        }

        is ShoppingListResult.Loading -> {
            ShoppingListUiState.Loading
        }

        is ShoppingListResult.Success -> {

            if (shoppingListResult.ingredients.isEmpty()) {
                ShoppingListUiState.Empty
            } else {

                val recipeToIngredientMap =
                    shoppingListResult.ingredients.groupBy { it.mappedRecipePreview }

                ShoppingListUiState.Content(
                    recipesWithIngredients = recipeToIngredientMap.mapNotNull { (recipe, ingredients) ->
                        recipe?.let { r ->
                            UiShoppingListRecipe(
                                id = recipe.id,
                                title = recipe.title,
                                imageUrl = recipe.image,
                                recipe = recipe,
                                ingredients = ingredients.map { ingredient ->
                                    UiShoppingListIngredient(
                                        ingredient = ingredient,
                                        isChecked = mutableStateOf(ingredient.isPurchased)
                                    )
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}