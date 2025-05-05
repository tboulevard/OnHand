package com.tstreet.onhand.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.ui.UiShoppingListRecipe

@Composable
fun ShoppingListRecipeCards(
    recipePreviews: List<UiShoppingListRecipe>,
    onItemClick: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO: Implement item keys for this approach to avoid recompositions, currently
        //  doesn't appear to work...
        itemsIndexed(recipePreviews) { index, recipe ->
            ShoppingListRecipeItem(
                ShoppingListRecipeCard(
                    recipePreview = recipe,
                    onItemClick = onItemClick,
                    onRemoveClick = onRemoveClick
                )
            )
        }
    }
}

@Composable
fun ShoppingListRecipeItem(
    card: ShoppingListRecipeCard
) {
    Surface(
        modifier = Modifier
            .size(148.dp)
            .padding(8.dp),
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = card.recipePreview.imageUrl,
                    contentDescription = null
                )
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "clear ingredients",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .clickable { card.onRemoveClick() },
                    tint = MaterialTheme.colorScheme.inverseSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { card.onItemClick("recipe_detail/${card.recipePreview.id}") }
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = card.recipePreview.title,
                        modifier = Modifier
                            .padding(4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

class ShoppingListRecipeCard(
    val recipePreview: UiShoppingListRecipe,
    val onItemClick: (String) -> Unit,
    val onRemoveClick: () -> Unit
)
