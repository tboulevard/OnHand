package com.tstreet.onhand.feature.recipesearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.ui.FullScreenProgressIndicator

@Composable
fun RecipeSearchScreen(
    viewModel: RecipeSearchViewModel
) {
    val isSearching by viewModel.isSearching.collectAsState()
    // TODO: use collectAsStateWithLifecycle instead, but research why first
    val recipes by viewModel.recipes.collectAsState()

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            isSearching -> {
                FullScreenProgressIndicator()
            }
            else -> {
                RecipeSearchCardList(
                    recipes = recipes,
                    onItemClick = viewModel::onRecipeClicked,
                    onSaveClick = viewModel::onRecipeSaved
                )
            }
        }
    }
}

class RecipeSearchCard(
    val title: String,
    val usedIngredients: Int,
    val likes: Int
)

@Composable
fun RecipeSearchCardList(
    recipes: List<Recipe>,
    onItemClick: (Int) -> Unit,
    onSaveClick: (Int) -> Unit
) {
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
                index = index,
                onItemClick = onItemClick,
                onSaveClick = onSaveClick
            )
        }
    }
}

@Preview
@Composable
fun RecipeSearchCardItem(
    @PreviewParameter(FeatureScreenPreviewParamProvider::class) card: RecipeSearchCard,
    index: Int = 0,
    onItemClick: (Int) -> Unit = { },
    onSaveClick: (Int) -> Unit = { }
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp
            ),
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.clickable { onItemClick(index) },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(text = card.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "${card.usedIngredients} ingredients used",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(text = "${card.likes} likes", style = MaterialTheme.typography.bodySmall)
            }
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "save",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onSaveClick(index) },
                )
            }
        }

    }
}

// TODO: move to a better location
class FeatureScreenPreviewParamProvider : PreviewParameterProvider<RecipeSearchCard> {
    override val values: Sequence<RecipeSearchCard> =
        sequenceOf(RecipeSearchCard("Recipe1", 10, 100))
}




