package com.tstreet.onhand.feature.reciperesult

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.Ingredient

//@Preview // TODO: why doesn't this work with args on function?
@Composable
// TODO: How do I inject the viewmodel?
fun RecipeResultScreen(
    viewModel: RecipeResultViewModel
) {
    var text by remember {
        mutableStateOf("")
    }
    var searchResults by remember {
        mutableStateOf(listOf<Ingredient>())
    }
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
                    // TODO:
                    if (text.isNotBlank()) {
                        searchResults = viewModel.search(text)
                    }
                }
            ) {
                Text(text = "Search Recipes")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                searchResults
            ) {currentItem ->
                Text(
                    modifier = Modifier.size(36.dp),
                    text = currentItem.name)
            }
        }
    }
}