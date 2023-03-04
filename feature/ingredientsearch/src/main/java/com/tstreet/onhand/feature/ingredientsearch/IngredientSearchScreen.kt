package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

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
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                placeholder = { Text(text = "Search Ingredients") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ingredients) { ingredient ->
                    Button(
                        onClick = {
                            viewModel.addIngredientToPantry(ingredient)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = ingredient.name)
                    }
                }
            }
        }
    }
}