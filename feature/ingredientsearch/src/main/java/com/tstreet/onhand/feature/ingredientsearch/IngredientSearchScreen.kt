package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tstreet.onhand.core.model.Ingredient
import kotlinx.coroutines.launch

@Composable
fun IngredientSearchScreen(
    navController: NavController,
    viewModel: IngredientSearchViewModel
) {
    var text by remember {
        mutableStateOf("")
    }
    val searchResults by viewModel.currentSearchResults.collectAsState()

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
                value = text,
                onValueChange = {
                    text = it
                }
            )
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        // Go through: https://developer.android.com/jetpack/compose/state-saving
                        // figure out why just setting the searchResults value doesn't update
                        // recyclerview
                        viewModel.search(text)
                    }
                }
            ) {
                Text(text = "Search Ingredients")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(
                searchResults
            ) { currentItem ->
                Button(
                    onClick = {
                        viewModel.addIngredientToPantry(currentItem)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = currentItem.name)
                }
            }
        }
    }
}