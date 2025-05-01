package com.tstreet.onhand.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.common.RECIPE_DETAIL_ROUTE
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.ui.RecipeSaveState
import com.tstreet.onhand.core.model.ui.RecipeWithSaveState

@Composable
fun RecipeCardList(
    recipes: List<RecipeWithSaveState>,
    onItemClick: (String) -> Unit,
    onSaveClick: (RecipeWithSaveState) -> Unit,
    onUnSaveClick: (RecipeWithSaveState) -> Unit,
    onAddToShoppingListClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        itemsIndexed(recipes) { index, item ->
            RecipeCardItem(
                recipeWithSaveState = item,
                index = index,
                onItemClick = onItemClick,
                onSaveClick = onSaveClick,
                onUnSaveClick = onUnSaveClick,
                onAddToShoppingListClick = onAddToShoppingListClick
            )
        }
    }
}

@Composable
fun RecipeCardItem(
    recipeWithSaveState: RecipeWithSaveState,
    index: Int = 0,
    onItemClick: (String) -> Unit = { },
    onSaveClick: (RecipeWithSaveState) -> Unit = { },
    onUnSaveClick: (RecipeWithSaveState) -> Unit = { },
    onAddToShoppingListClick: (Int) -> Unit = { }
) {
    val recipe = recipeWithSaveState.preview

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onItemClick("$RECIPE_DETAIL_ROUTE/${recipe.id}") },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (recipeWithSaveState.saveState.value == RecipeSaveState.SAVED) 4.dp else 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (recipeWithSaveState.saveState.value == RecipeSaveState.SAVED) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with title and save icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (recipeWithSaveState.preview.isCustom) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Custom Recipe",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (recipeWithSaveState.saveState.value == RecipeSaveState.SAVED) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                "Saved",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                // Save/Unsave button
                when (recipeWithSaveState.saveState.value) {
                    RecipeSaveState.SAVED -> {
                        IconButton(onClick = { onUnSaveClick(recipeWithSaveState) }) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Remove from saved recipes",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    RecipeSaveState.NOT_SAVED -> {
                        IconButton(onClick = { onSaveClick(recipeWithSaveState) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Save recipe",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    RecipeSaveState.LOADING -> {
                        // TODO: Refactor to show loading indicator later, if needed.
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Loading recipe",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Recipe info
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                if (recipe.usedIngredientCount > 0) {
                    Text(
                        text = "${if (recipe.usedIngredientCount > 1) "${recipe.usedIngredientCount} ingredients" else "1 ingredient"} used",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (recipe.missedIngredientCount > 0) {
                    Text(
                        text = "${if (recipe.missedIngredientCount > 1) "${recipe.missedIngredientCount} ingredients" else "1 ingredient"} missing",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Favorite,
                        contentDescription = "Likes",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.likes} likes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Add to shopping list button
            Button(
                onClick = { onAddToShoppingListClick(index) },
                enabled = recipe.missedIngredientCount > 0,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (recipe.missedIngredientCount > 0) {
                    Icon(
                        Icons.Outlined.ShoppingCart,
                        contentDescription = "Add to shopping cart",
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        "Add missing ingredients",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "All ingredients available",
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 8.dp)
                    )
                    Text("You have all ingredients")
                }
            }
        }
    }
}

@Preview
@Composable
fun RecipeCardItemPreview(
    @PreviewParameter(RecipeCardPreviewParamProvider::class) recipeWithSaveState: RecipeWithSaveState
) {
    RecipeCardItem(
        recipeWithSaveState = recipeWithSaveState
    )
}

class RecipeCardPreviewParamProvider : PreviewParameterProvider<RecipeWithSaveState> {
    override val values: Sequence<RecipeWithSaveState> = sequenceOf(
        RecipeWithSaveState(
            RecipePreview(
                id = 1,
                title = "A very long recipe name that is very long",
                image = "image",
                imageType = "imageType",
                usedIngredientCount = 10,
                usedIngredients = emptyList(),
                missedIngredientCount = 3,
                missedIngredients = emptyList(),
                likes = 100,
                isCustom = true
            ),
            saveState = mutableStateOf(RecipeSaveState.SAVED),
        )
    )
}
