package com.tstreet.onhand.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tstreet.onhand.core.common.RECIPE_DETAIL_ROUTE
import com.tstreet.onhand.core.model.RecipePreview
import com.tstreet.onhand.core.model.ui.IngredientAvailability
import com.tstreet.onhand.core.model.ui.RecipeSaveState
import com.tstreet.onhand.core.model.ui.RecipeWithSaveState
import kotlinx.coroutines.delay

@Composable
fun RecipeCardList(
    recipes: List<RecipeWithSaveState>,
    onItemClick: (String) -> Unit,
    onSaveClick: (RecipeWithSaveState) -> Unit,
    onUnSaveClick: (RecipeWithSaveState) -> Unit,
    onAddToShoppingListClick: (RecipeWithSaveState) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(recipes) { _, item ->
            RecipeCardItem(
                recipeWithSaveState = item,
                onItemClick = onItemClick,
                onSaveClick = onSaveClick,
                onUnSaveClick = onUnSaveClick,
                onAddToShoppingListClick = onAddToShoppingListClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCardItem(
    recipeWithSaveState: RecipeWithSaveState,
    onItemClick: (String) -> Unit = { },
    onSaveClick: (RecipeWithSaveState) -> Unit = { },
    onUnSaveClick: (RecipeWithSaveState) -> Unit = { },
    onAddToShoppingListClick: (RecipeWithSaveState) -> Unit = { }
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
                    Box(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
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
            RecipePreviewInfo(
                recipe = recipe,
                recipeWithSaveState = recipeWithSaveState
            )

            Spacer(modifier = Modifier.height(8.dp))

//            // Display missing ingredients in cart if any
//            if (recipeWithSaveState.ingredientShoppingCartState.value == IngredientAvailability.MISSING_INGREDIENTS) {
//                Row(
//                    modifier = Modifier.padding(vertical = 4.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Surface(
//                        shape = MaterialTheme.shapes.small,
//                        color = MaterialTheme.colorScheme.tertiaryContainer,
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                        ) {
//                            Icon(
//                                Icons.Default.ShoppingCart,
//                                contentDescription = "Missing ingredients in cart",
//                                modifier = Modifier.size(16.dp),
//                                tint = MaterialTheme.colorScheme.tertiary
//                            )
//                            Spacer(modifier = Modifier.width(4.dp))
//                            Text(
//                                text = "${recipeWithSaveState.ingredientShoppingCartState.value.size} in shopping cart",
//                                style = MaterialTheme.typography.labelMedium,
//                                color = MaterialTheme.colorScheme.onTertiaryContainer
//                            )
//                        }
//                    }
//                }
//            }

            Spacer(modifier = Modifier.height(8.dp))

            AddToShoppingListButton(
                onAddToShoppingListClick,
                recipeWithSaveState
            )
        }
    }
}


@Composable
fun RecipePreviewInfo(
    recipe: RecipePreview,
    recipeWithSaveState: RecipeWithSaveState
) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
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

        // Missing ingredient info on recipe
        when (recipeWithSaveState.ingredientPantryState.value) {
            IngredientAvailability.MISSING_INGREDIENTS -> {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Missing ingredients",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (recipe.missedIngredientCount == 1) "Missing 1 ingredient" else "Missing ${recipe.missedIngredientCount} ingredients",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            IngredientAvailability.ALL_INGREDIENTS_AVAILABLE -> {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = "You have all ingredients",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "You have all ingredients",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddToShoppingListButton(
    onClick: (RecipeWithSaveState) -> Unit,
    recipeWithSaveState: RecipeWithSaveState
) {
    // Calculate button enabled state
    val isEnabled = if (!recipeWithSaveState.pantrySatisfiesIngredients()) {
        !recipeWithSaveState.shoppingCartSatisfiesIngredients()
    } else {
        false
    }

    // Add to shopping list button
    Button(
        onClick = { onClick(recipeWithSaveState) },
        // Should be disabled if:
        // 1. All ingredients to make recipe are present in the pantry
        // 2. All ingredients in the shopping cart + pantry meet condition to make recipe
        // TODO: If two recipes share ingredients, they both will not update in real time
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (!recipeWithSaveState.pantrySatisfiesIngredients()) {
            if (!recipeWithSaveState.shoppingCartSatisfiesIngredients()) {
                // What's in our shopping cart does not satisfy the recipe
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
                // What's in our shopping cart does satisfy the recipe
                Icon(
                    Icons.Default.Check,
                    contentDescription = "All ingredients in shopping cart",
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 8.dp)
                )
                Text("All missing ingredients in shopping cart")
            }
        } else {
            // We have all ingredients in Pantry
            Icon(
                Icons.Default.Check,
                contentDescription = "You have all ingredients",
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 8.dp)
            )
            Text("You have all ingredients")
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
                title = "Homemade Pizza with Fresh Toppings",
                image = "https://spoonacular.com/recipeImages/511728-312x231.jpg",
                imageType = "jpg",
                usedIngredientCount = 10,
                usedIngredients = emptyList(),
                missedIngredientCount = 3,
                missedIngredients = emptyList(),
                likes = 1024,
                isCustom = true
            ),
            saveState = mutableStateOf(RecipeSaveState.SAVED),
            ingredientPantryState = mutableStateOf(IngredientAvailability.ALL_INGREDIENTS_AVAILABLE),
            ingredientShoppingCartState = mutableStateOf(IngredientAvailability.ALL_INGREDIENTS_AVAILABLE)
        )
    )
}