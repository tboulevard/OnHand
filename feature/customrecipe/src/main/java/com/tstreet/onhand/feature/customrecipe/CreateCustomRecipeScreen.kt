package com.tstreet.onhand.feature.customrecipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.common.INGREDIENT_SEARCH_ROUTE

// TODO: use @PreviewParameter + create module with fake models to populate composables
// TODO: screen rotation wipes `isSearchBarFocused` -> look into used collectAsStateWithLifecycle
// TODO: Using the hardware back/swipe back while in search doesn't nav back to pantry. Eventually
//  we'll probably want IngredientSearch as a separate screen so we can add it to the nav backstack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomRecipeScreen(
    navController: NavHostController,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { navController.navigate(INGREDIENT_SEARCH_ROUTE)},
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "add ingredients",
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.surfaceTint
            )
            Text("Add ingredients")
        }
        Button(
            onClick = viewModel::onDoneClicked
        ) {
            Text(text = "Done")
        }
    }
}