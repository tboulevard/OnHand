package com.tstreet.onhand.feature.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.IngredientCategory
import com.tstreet.onhand.core.model.ui.home.HomeUiState
import com.tstreet.onhand.core.model.ui.home.PantryRowItem
import com.tstreet.onhand.core.model.ui.home.SelectableIngredientCategory
import com.tstreet.onhand.core.model.ui.home.UiPantryIngredientV2

class HomeUiStatePreviewParameterProvider :
    PreviewParameterProvider<HomeUiState> {
    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(
            filterCategories = listOf(
                SelectableIngredientCategory(
                    IngredientCategory.ALL,
                    mutableStateOf(true)
                ),
                SelectableIngredientCategory(
                    IngredientCategory.PRODUCE,
                    mutableStateOf(false)
                ),
                SelectableIngredientCategory(
                    IngredientCategory.DAIRY_AND_EGGS,
                    mutableStateOf(false)
                )
            ),
            pantryRows = listOf(
                PantryRowItem.Header(
                    IngredientCategory.PRODUCE
                ),
                PantryRowItem.Ingredient(
                    UiPantryIngredientV2(
                        Ingredient(
                            id = 1,
                            name = "Broccoli",
                            IngredientCategory.PRODUCE
                        ),
                        mutableStateOf(true),
                    )
                ),
                PantryRowItem.Ingredient(
                    UiPantryIngredientV2(
                        Ingredient(
                            id = 2,
                            name = "Carrot",
                            IngredientCategory.PRODUCE
                        ),
                        mutableStateOf(false)
                    )
                ),
                PantryRowItem.Header(
                    IngredientCategory.MEAT_AND_SEAFOOD
                ),
                PantryRowItem.Ingredient(
                    UiPantryIngredientV2(
                        Ingredient(
                            id = 3,
                            name = "Chicken Thigh",
                            IngredientCategory.MEAT_AND_SEAFOOD
                        ),
                        mutableStateOf(false)
                    )
                ),
                PantryRowItem.Header(
                    IngredientCategory.DAIRY_AND_EGGS
                ),
                PantryRowItem.Ingredient(
                    UiPantryIngredientV2(
                        Ingredient(
                            id = 4,
                            name = "Whole Milk",
                            IngredientCategory.DAIRY_AND_EGGS
                        ),
                        mutableStateOf(true)
                    )
                ),
            )
        )
    )
}
