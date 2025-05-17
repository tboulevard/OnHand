package com.tstreet.onhand.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(recipePreviews) { _, recipe ->
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
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(170.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { card.onItemClick("recipe_detail/${card.recipePreview.id}") }
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                model = card.recipePreview.imageUrl,
                contentDescription = "Recipe thumbnail",
                contentScale = ContentScale.Crop
            )
            
            IconButton(
                onClick = { card.onRemoveClick() },
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Remove recipe",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = card.recipePreview.title,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}

class ShoppingListRecipeCard(
    val recipePreview: UiShoppingListRecipe,
    val onItemClick: (String) -> Unit,
    val onRemoveClick: () -> Unit
)