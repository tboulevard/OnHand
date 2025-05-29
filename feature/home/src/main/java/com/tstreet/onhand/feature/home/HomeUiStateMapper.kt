package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.GetPantryResult
import com.tstreet.onhand.core.model.ui.home.HomeViewUiStateV2
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import javax.inject.Inject

@FeatureScope
class HomeUiStateMapper @Inject constructor() {

    fun mapToHomeUiState(
        ingredientCategories: List<SelectableIngredientCategory>,
        getPantryResult: GetPantryResult
    ): HomeViewUiStateV2 {
        return when (getPantryResult) {
            GetPantryResult.Error -> {
                HomeViewUiStateV2.Error
            }

            GetPantryResult.Loading -> {
                HomeViewUiStateV2.Loading
            }

            is GetPantryResult.Success -> {
                HomeViewUiStateV2.Content(
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
                                            ingredient.ingredient.name,
                                            entry.key,
                                            inPantry = mutableStateOf(ingredient.inPantry),
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