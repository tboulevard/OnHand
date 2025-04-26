package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.IngredientSearchResult
import com.tstreet.onhand.core.model.domain.PantryListResult
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import javax.inject.Inject

@FeatureScope
class HomeUiStateMapper @Inject constructor() {

    fun mapSearchResultToSearchUi(
        searchResult: IngredientSearchResult
    ): SearchUiState = when (searchResult) {
        is IngredientSearchResult.Success -> {
            if (searchResult.ingredients.isEmpty()) {
                SearchUiState.Empty
            } else {
                SearchUiState.Content(
                    ingredients = searchResult.ingredients.map {
                        UiPantryIngredient(
                            ingredient = it.ingredient,
                            inPantry = mutableStateOf(it.inPantry)
                        )
                    }
                )
            }
        }

        is IngredientSearchResult.Error -> {
            SearchUiState.Error
        }

        is IngredientSearchResult.Loading -> {
            SearchUiState.Loading
        }
    }

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
}