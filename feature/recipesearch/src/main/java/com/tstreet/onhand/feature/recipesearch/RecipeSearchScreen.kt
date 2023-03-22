package com.tstreet.onhand.feature.recipesearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        when {
            isSearching -> {
                ProgressIndicator()
            }
            else -> {
                RecipeSearchCardList(
                    recipes = viewModel.recipes,
                    onItemClick = viewModel::onRecipeClicked
                )
            }
        }
    }
}

@Composable
fun ProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

data class RecipeSearchCard(
    val title: String,
    val usedIngredients: Int,
    val likes: Int
)

@Composable
fun RecipeSearchCardList(recipes: List<Recipe>, onItemClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        itemsIndexed(recipes) { index, recipe ->
            RecipeSearchCardItem(
                card = RecipeSearchCard(
                    title = recipe.title,
                    usedIngredients = recipe.usedIngredientCount,
                    likes = recipe.likes
                ),
                onItemClick = onItemClick,
                index = index
            )
        }
    }
}

@Composable
fun RecipeSearchCardItem(card: RecipeSearchCard, index: Int, onItemClick: (Int) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp
            )
            .clickable { onItemClick(index) },
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = card.title, style = MaterialTheme.typography.headlineMedium)
            Text(text = "${card.usedIngredients} ingredients used", style = MaterialTheme.typography.bodyMedium)
            Text(text = "${card.likes} likes", style = MaterialTheme.typography.bodySmall)
        }
    }
}




