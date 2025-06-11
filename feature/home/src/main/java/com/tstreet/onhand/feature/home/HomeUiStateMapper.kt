package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import com.tstreet.onhand.core.common.FeatureScope
import com.tstreet.onhand.core.model.domain.GetPantryResult
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2
import javax.inject.Inject

@FeatureScope
class HomeUiStateMapper @Inject constructor() {

    fun mapToPantryUi(
        getPantryResult: GetPantryResult
    ): List<PantryRowItem> {
        return when (getPantryResult) {
            GetPantryResult.Error -> {
                // TODO: change how this is handled
                emptyList()
            }

            is GetPantryResult.Success -> {
                getPantryResult.ingredients.groupBy {
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
            }
        }
    }
}