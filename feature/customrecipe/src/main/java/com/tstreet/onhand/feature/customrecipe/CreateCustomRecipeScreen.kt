package com.tstreet.onhand.feature.customrecipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.common.INGREDIENT_SEARCH_ROUTE
import com.tstreet.onhand.core.ui.INGREDIENT_SEARCH_ITEMS_KEY
import com.tstreet.onhand.core.ui.OnHandScreenHeader

// TODO: use @PreviewParameter + create module with fake models to populate composables
// TODO: screen rotation wipes `isSearchBarFocused` -> look into used collectAsStateWithLifecycle
// TODO: Using the hardware back/swipe back while in search doesn't nav back to pantry. Eventually
//  we'll probably want IngredientSearch as a separate screen so we can add it to the nav backstack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomRecipeScreen(
    navController: NavHostController,
    savedStateHandle: SavedStateHandle,
    viewModel: CreateCustomRecipeViewModel
) {

    // TODO: nav away warn unsaved changes

    val title = viewModel.title.collectAsState()
    val ingredients = viewModel.ingredients
    val instructions = viewModel.instructions.collectAsState()

    LaunchedEffect(null) {
        viewModel.onReceiveIngredients(
            savedStateHandle[INGREDIENT_SEARCH_ITEMS_KEY] ?: emptyList()
        )
    }

    Column() {
        OnHandScreenHeader(text = "Create Recipe")
        TextField(
            value = title.value,
            onValueChange = viewModel::onTitleChanged,
            label = { Text("Recipe Title") },
            textStyle = MaterialTheme.typography.bodyMedium
        )
        IconButton(
            modifier = Modifier.size(144.dp),
            onClick = { viewModel.onImageChanged("") }
        ) {
            Column() {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "add image",
                    modifier = Modifier.fillMaxWidth(),
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
                Text(text = "Add Cover Image")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { navController.navigate(INGREDIENT_SEARCH_ROUTE) },
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

        // TODO: if this list exceeds bounds of screen, only this section is scrollable.
        //  make entire screen scrollable (similar approach to shopping list)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(ingredients) { index, item ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.ingredient.name)
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "remove ingredient",
                        modifier = Modifier
                            .clickable { viewModel.onRemoveIngredient(index) }
                            .size(32.dp)
                            .padding(4.dp)
                            .align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = instructions.value,
            onValueChange = viewModel::onInstructionsChanged,
            label = { Text("Instructions (optional)") },
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = viewModel::onDoneClicked
        ) {
            Text(text = "Done")
        }
    }
}