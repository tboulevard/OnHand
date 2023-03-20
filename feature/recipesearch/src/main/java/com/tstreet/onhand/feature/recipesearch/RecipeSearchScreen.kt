package com.tstreet.onhand.feature.recipesearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.Recipe

@Composable
fun RecipeSearchScreen(
    viewModel: RecipeSearchViewModel
) {

    val isSearching by viewModel.isSearching.collectAsState()

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                itemsIndexed(
                    items = viewModel.recipes
                ) { index, ingredient ->
                    RecipeSearchListItem(
                        recipe = ingredient,
                        index,
                        onItemClicked = {  } // TODO: implement saving
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeSearchListItem(recipe: Recipe, index: Int, onItemClicked: Any) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        ) {
            Text(text = recipe.title)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Text(text = recipe.image)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Text(text = "Likes: "+ recipe.likes)
        }
    }
}

