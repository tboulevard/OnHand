package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.model.domain.IngredientSearchResult
import com.tstreet.onhand.core.model.ui.HomeViewUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import javax.inject.Inject

class HomeUiStateMapper @Inject constructor() {

    fun mapSearchResultToHomeUi(
        searchResult: IngredientSearchResult
    ): HomeViewUiState = when (searchResult) {
        is IngredientSearchResult.Success -> {
            if (searchResult.ingredients.isEmpty()) {
                HomeViewUiState.Empty
            } else {
                HomeViewUiState.Content(
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
            HomeViewUiState.Error
        }

        is IngredientSearchResult.Loading -> {
            HomeViewUiState.Loading
        }
    }
}