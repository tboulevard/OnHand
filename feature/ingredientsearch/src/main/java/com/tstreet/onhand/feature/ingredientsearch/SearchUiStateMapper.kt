package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.IngredientSearchResult
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import javax.inject.Inject

@FeatureScope
class SearchUiStateMapper @Inject constructor() {

    fun mapSearchResultToSearchUi(
        searchResult: IngredientSearchResult
    ): SearchUiState = when (searchResult) {
        is IngredientSearchResult.Success -> {
            if (searchResult.ingredients.isEmpty()) {
                SearchUiState.Empty
            } else {
                SearchUiState.Content(
                    ingredients = searchResult.ingredients.map {
                        UiSearchIngredient(
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
}