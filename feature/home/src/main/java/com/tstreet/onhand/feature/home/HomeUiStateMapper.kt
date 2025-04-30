package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.PantryListResult
import com.tstreet.onhand.core.model.domain.SuggestedIngredientsResult
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import javax.inject.Inject

@FeatureScope
class HomeUiStateMapper @Inject constructor() {

    fun mapPantryListResultToPantryUi(
        pantryListResult: PantryListResult
    ): PantryUiState =
        when (pantryListResult) {
            is PantryListResult.Error -> {
                PantryUiState.Error
            }

            is PantryListResult.Loading -> {
                PantryUiState.Loading
            }

            is PantryListResult.Success -> {
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
}