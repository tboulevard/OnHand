package com.tstreet.onhand.feature.recipesearch

import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.RecipeSearchResult
import com.tstreet.onhand.core.model.ui.RecipeSearchUiState
import com.tstreet.onhand.core.model.ui.toRecipeWithSaveStateItemList
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