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
            ShoppingListUiState.Content(
                ingredients = shoppingListResult.ingredients.map {
                    UiShoppingListIngredient(
                        ingredient = it,
                        mappedRecipe = it.mappedRecipePreview?.let { recipe ->
                            UiShoppingListRecipe(
                                id = recipe.id,
                                title = recipe.title,
                                imageUrl = recipe.image,
                                recipePreview = recipe
                            )
                        },
                        isChecked = mutableStateOf(it.isPurchased)
                    )
                },
                mappedRecipes = shoppingListResult.mappedRecipes.map {
                    UiShoppingListRecipe(
                        id = it.id,
                        title = it.title,
                        imageUrl = it.image,
                        recipePreview = it
                    )
                }
            )
        }
    }
}