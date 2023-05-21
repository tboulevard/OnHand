package com.tstreet.onhand.feature.customrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

// TODO: use @PreviewParameter + create module with fake models to populate composables
// TODO: screen rotation wipes `isSearchBarFocused` -> look into used collectAsStateWithLifecycle
// TODO: Using the hardware back/swipe back while in search doesn't nav back to pantry. Eventually
//  we'll probably want IngredientSearch as a separate screen so we can add it to the nav backstack
@Composable
fun AddCustomRecipeScreen(
    viewModel: AddCustomRecipeViewModel
) {
    Box {
        Text(text = "AddCustomRecipeScreen")
    }
}