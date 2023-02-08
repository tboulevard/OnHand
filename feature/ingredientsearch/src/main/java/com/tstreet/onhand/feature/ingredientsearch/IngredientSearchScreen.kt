package com.tstreet.onhand.feature.ingredientsearch

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.tstreet.onhand.core.model.Ingredient

@Preview // TODO: why doesn't this work with args on function?
@Composable
// TODO: How do I inject the viewmodel?
fun IngredientSearchScreen() {
    var text by remember {
        mutableStateOf("")
    }
    var searchResults by remember {
        mutableStateOf(listOf<Ingredient>())
    }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
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
                    // searchResults = viewModel.search(text)
                    if (text.isNotBlank()) {
                        searchResults = searchResults + Ingredient(text)
                    }
                }
            ) {
                Text(text = "Search")
            }
        }
        LazyColumn {
            items(
                searchResults
            ) {currentItem ->
                Text(text = currentItem.name)
            }
        }
    }
}