package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.ui.home.HomeViewUiStateV2
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2

class HomeUiStatePreviewParameterProvider : PreviewParameterProvider<HomeViewUiStateV2> {
    override val values: Sequence<HomeViewUiStateV2> = sequenceOf(
        HomeViewUiStateV2.Content(
            pantryRows = listOf(
                PantryRowItem.Header(
                    IngredientCategory.PRODUCE
                ),
                PantryRowItem.Ingredient(
                    UiPantryIngredientV2(
                        "Broccoli",
                        IngredientCategory.PRODUCE,
                        mutableStateOf(true),
                        mutableStateOf(true)
                    )
                ),
                PantryRowItem.Ingredient(
                    UiPantryIngredientV2(
                        "Carrots",
                        IngredientCategory.PRODUCE,
                        mutableStateOf(false),
                        mutableStateOf(false)
                    )
                ),
                PantryRowItem.Header(
                    IngredientCategory.MEAT_AND_SEAFOOD
                ),
                PantryRowItem.Ingredient(
                    UiPantryIngredientV2(
                        "Chicken Thigh",
                        IngredientCategory.MEAT_AND_SEAFOOD,
                        mutableStateOf(true),
                        mutableStateOf(false)
                    )
                ),
            )
        )
    )
}
