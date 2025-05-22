package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.GetPantryResult
import com.tstreet.onhand.core.model.domain.SuggestedIngredientsResult
import com.tstreet.onhand.core.model.ui.home.HomeViewUiStateV2
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import javax.inject.Inject

@FeatureScope
class HomeUiStateMapper @Inject constructor() {

    fun mapPantryListResultToPantryUi(
        pantryListResult: GetPantryResult
    ): PantryUiState =
        when (pantryListResult) {
            is GetPantryResult.Error -> {
                PantryUiState.Error
            }

            is GetPantryResult.Loading -> {
                PantryUiState.Loading
            }

            is GetPantryResult.Success -> {
                PantryUiState.Content(
                    ingredients = pantryListResult.ingredients.map {
                        UiPantryIngredient(
                            ingredient = it.ingredient,
                            inPantry = mutableStateOf(it.inPantry)
                        )
                    }
                )
            }
        }

    fun mapSuggestedIngredientsToSearchUiState(
        suggestedIngredientsResult: SuggestedIngredientsResult
    ): SearchUiState =
        when (suggestedIngredientsResult) {
            is SuggestedIngredientsResult.Error -> {
                SearchUiState.Error
            }

            is SuggestedIngredientsResult.Loading -> {
                SearchUiState.Loading
            }

            is SuggestedIngredientsResult.Success -> {
                if (suggestedIngredientsResult.ingredients.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    SearchUiState.Content(
                        ingredients = suggestedIngredientsResult.ingredients.map {
                            UiSearchIngredient(
                                ingredient = it.ingredient,
                                inPantry = mutableStateOf(it.inPantry)
                            )
                        }
                    )
                }
            }
        }

    fun mapToHomeUiState(
        result: GetPantryResult
    ): HomeViewUiStateV2 {
        return when (result) {
            GetPantryResult.Error -> {
                HomeViewUiStateV2.Error
            }

            GetPantryResult.Loading -> {
                HomeViewUiStateV2.Loading
            }

            is GetPantryResult.Success -> {
                HomeViewUiStateV2.Content(
                    result.ingredients.groupBy {
                        it.ingredient.category
                    }.flatMap { entry ->
                        mutableListOf<PantryRowItem>(
                            PantryRowItem.Header(entry.key)
                        ).also {
                            it.addAll(
                                entry.value.map { ingredient ->
                                    PantryRowItem.Ingredient(
                                        UiPantryIngredientV2(
                                            ingredient.ingredient.name,
                                            entry.key,
                                            inPantry = mutableStateOf(true),
                                            inShoppingCart = mutableStateOf(false)
                                        )
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