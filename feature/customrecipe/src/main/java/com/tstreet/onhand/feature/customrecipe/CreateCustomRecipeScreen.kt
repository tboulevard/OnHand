package com.tstreet.onhand.feature.customrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// TODO: use @PreviewParameter + create module with fake models to populate composables
// TODO: screen rotation wipes `isSearchBarFocused` -> look into used collectAsStateWithLifecycle
// TODO: Using the hardware back/swipe back while in search doesn't nav back to pantry. Eventually
//  we'll probably want IngredientSearch as a separate screen so we can add it to the nav backstack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomRecipeScreen(
    viewModel: CreateCustomRecipeViewModel
) {

    val recipeTitle = viewModel.recipeTitle.collectAsState()

    Box {
        Text(text = "AddCustomRecipeScreen")
    }

    Column() {
        TextField(
            value = recipeTitle.value,
            onValueChange = viewModel::onTitleChanged,
            label = { Text("Recipe Title") },
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = viewModel::onDoneClicked
        ) {
            Text(text = "Done")
        }
    }
}