package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.tstreet.onhand.core.model.Ingredient

@Composable
fun IngredientSearchScreen(
    navController: NavController,
    viewModel: IngredientSearchViewModel
) {
    val searchText by viewModel.searchText.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                placeholder = { Text(text = "Search Ingredients") }
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // TODO: fix unchecked type casting, for some reason viewModel.ingredients
            // isn't recognizing the underlying List<Ingredient> type and only
            // emitting List<Any>
            items(ingredients) { ingredient ->
                Button(
                    onClick = {
                        viewModel.addIngredientToPantry(ingredient)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = ingredient.name)
                }
            }
        }
    }
}