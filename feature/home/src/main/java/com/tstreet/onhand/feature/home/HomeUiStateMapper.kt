package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.GetPantryResult
import com.tstreet.onhand.core.model.ui.home.HomeUiState
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import javax.inject.Inject

@FeatureScope
class HomeUiStateMapper @Inject constructor() {

    fun mapToHomeUiState(
        getPantryResult: GetPantryResult
    ): HomeUiState {
        return when (getPantryResult) {
            GetPantryResult.Error -> {
                HomeUiState.Error
            }

            GetPantryResult.Loading -> {
                HomeUiState.Loading
            }

            is GetPantryResult.Success -> {
                HomeUiState.Content(
                    pantryRows = getPantryResult.ingredients.groupBy {
                        it.ingredient.category
                    }.flatMap { entry ->
                        mutableListOf<PantryRowItem>(
                            PantryRowItem.Header(entry.key)
                        ).also {
                            it.addAll(
                                entry.value.map { ingredient ->
                                    PantryRowItem.Ingredient(
                                        UiPantryIngredientV2(
                                            ingredient.ingredient,
                                            inPantry = mutableStateOf(ingredient.inPantry)
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