package com.tstreet.onhand.feature.shoppinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem
import com.tstreet.onhand.core.model.ui.ShoppingListUiState
import com.tstreet.onhand.core.model.ui.UiShoppingListIngredient
import com.tstreet.onhand.core.model.ui.UiShoppingListRecipe
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem.RecipeIngredientGroup
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem.StandaloneIngredient
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem.Summary
import com.tstreet.onhand.core.ui.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel
) {
    val uiState by viewModel.shoppingListUiState.collectAsStateWithLifecycle()
    val errorDialogState by viewModel.errorDialogState.collectAsStateWithLifecycle()
    val addIngredientDialogState = remember { mutableStateOf(AlertDialogState.dismissed()) }

    OnHandAlertDialog(
        onDismiss = viewModel::dismissErrorDialog,
        state = errorDialogState
    )

    OnHandAlertDialog(
        onDismiss = { addIngredientDialogState.value = AlertDialogState.dismissed() },
        state = addIngredientDialogState.value
    )

    when (uiState) {
        ShoppingListUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        is ShoppingListUiState.Content -> {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed((uiState as ShoppingListUiState.Content).screenContent()) { _, item ->
                    when (item) {
                        is UiShoppingListRowItem.Header -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.text,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                FilledTonalIconButton(
                                    onClick = {
                                        addIngredientDialogState.value = AlertDialogState.displayed(
                                            "Add Ingredient", "Feature coming soon!"
                                        )
                                    },
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add item manually",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        is Summary -> {
                            SummaryCard(summary = item)
                        }
                        is RecipeIngredientGroup -> {
                            RecipeCardWithIngredientsGroup(
                                recipeGroup = item,
                                onMarkIngredient = viewModel::onCheckOffShoppingIngredient,
                                onUnmarkIngredient = viewModel::onUncheckShoppingIngredient,
                                onRemoveIngredient = viewModel::onRemoveIngredient,
                                onAddIngredient = viewModel::onAddIngredient,
                                onRemoveRecipe = viewModel::onRemoveRecipe,
                                onUndoRemoveRecipe = viewModel::onUndoRemoveRecipe
                            )
                        }

                        is StandaloneIngredient -> {
                            when {
                                item.ingredients.isNotEmpty() -> {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        ) {
                                            Text(
                                                text = "Additional Items",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(
                                                    horizontal = 16.dp,
                                                    vertical = 4.dp
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            ShoppingListIngredientCards(
                                                ingredients = item.ingredients,
                                                onMarkIngredient = viewModel::onCheckOffShoppingIngredient,
                                                onUnmarkIngredient = viewModel::onUncheckShoppingIngredient,
                                                onRemoveIngredient = viewModel::onRemoveIngredient,
                                                onAddIngredient = viewModel::onAddIngredient
                                            )
                                        }
                                    }
                                }

                                else -> {
                                    EmptyShoppingListMessage()
                                }
                            }
                        }
                    }
                }
            }
        }

        ShoppingListUiState.Error -> {
            ErrorMessage()
        }

        ShoppingListUiState.Empty -> {
            EmptyShoppingListMessage()
        }
    }
}

@Composable
fun SummaryCard(summary: Summary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = summary.text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun ErrorMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                Button(
                    onClick = { /* Retry action */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun EmptyShoppingListMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Outlined.ShoppingCart,
                    contentDescription = "Empty shopping cart",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Your shopping list is empty",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Add items from recipes or manually enter your own",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCardWithIngredientsGroup(
    recipeGroup: RecipeIngredientGroup,
    onRemoveRecipe: (UiShoppingListRecipe) -> Unit,
    onUndoRemoveRecipe: (UiShoppingListRecipe) -> Unit,
    onMarkIngredient: (UiShoppingListIngredient) -> Unit,
    onUnmarkIngredient: (UiShoppingListIngredient) -> Unit,
    onRemoveIngredient: (UiShoppingListIngredient) -> Unit,
    onAddIngredient: (UiShoppingListIngredient) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        label = "arrowRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (recipeGroup.recipe.isInCart.value) 1f else 0.5f)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = recipeGroup.recipe.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${recipeGroup.getCheckedIngredients()} of ${recipeGroup.getTotalIngredients()} items collected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                if (recipeGroup.recipe.isInCart.value) {
                                    onRemoveRecipe(recipeGroup.recipe)
                                } else {
                                    onUndoRemoveRecipe(
                                        recipeGroup.recipe
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (recipeGroup.recipe.isInCart.value)
                                    Icons.Outlined.Delete
                                else
                                    Icons.Default.Refresh,
                                contentDescription = if (recipeGroup.recipe.isInCart.value)
                                    "Remove recipe"
                                else
                                    "Restore recipe",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        IconButton(
                            onClick = { expanded = !expanded }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = if (expanded) "Collapse" else "Expand",
                                modifier = Modifier.graphicsLayer {
                                    rotationZ = rotationState
                                },
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = recipeGroup.getCollectionProgress(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ShoppingListIngredientCards(
                    ingredients = recipeGroup.recipe.ingredients,
                    onMarkIngredient = onMarkIngredient,
                    onUnmarkIngredient = onUnmarkIngredient,
                    onRemoveIngredient = onRemoveIngredient,
                    onAddIngredient = onAddIngredient
                )
            }
        }
    }
}

@Composable
fun ShoppingListIngredientCards(
    ingredients: List<UiShoppingListIngredient>,
    onMarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onUnmarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onRemoveIngredient: (UiShoppingListIngredient) -> Unit = { },
    onAddIngredient: (UiShoppingListIngredient) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        ingredients.forEach { ingredient ->
            ShoppingListCardItem(
                ingredient = ingredient,
                onMarkIngredient = onMarkIngredient,
                onUnmarkIngredient = onUnmarkIngredient,
                onRemoveIngredient = onRemoveIngredient,
                onAddIngredient = onAddIngredient
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListCardItem(
    ingredient: UiShoppingListIngredient,
    onMarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onUnmarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onRemoveIngredient: (UiShoppingListIngredient) -> Unit = { },
    onAddIngredient: (UiShoppingListIngredient) -> Unit
) {
    var isChecked by remember { mutableStateOf(ingredient.isChecked.value) }

    // Animated checkbox scale on state change
    var showCheckAnimation by remember { mutableStateOf(false) }
    val checkScale by animateFloatAsState(
        targetValue = if (showCheckAnimation) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkboxScale"
    )

    LaunchedEffect(ingredient.isChecked.value) {
        isChecked = ingredient.isChecked.value
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (ingredient.isInCart.value) 1f else 0.5f)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Standard Material 3 Checkbox
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    showCheckAnimation = true
                    if (isChecked) {
                        onUnmarkIngredient(ingredient)
                    } else {
                        onMarkIngredient(ingredient)
                    }
                },
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = checkScale
                        scaleY = checkScale
                    },
                enabled = ingredient.isInCart.value,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            LaunchedEffect(showCheckAnimation) {
                if (showCheckAnimation) {
                    delay(200)
                    showCheckAnimation = false
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = ingredient.ingredient.ingredient.name,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isChecked)
                        TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isChecked)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }

            // Show remove/restore button
            IconButton(
                onClick = {
                    if (ingredient.isInCart.value) {
                        onRemoveIngredient(ingredient)
                    } else {
                        onAddIngredient(ingredient)
                    }
                }
            ) {
                Icon(
                    imageVector = if (ingredient.isInCart.value)
                        Icons.Default.Clear
                    else
                        Icons.Default.Refresh,
                    contentDescription = if (ingredient.isInCart.value)
                        "Remove item"
                    else
                        "Restore item",
                    tint = if (ingredient.isInCart.value)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
