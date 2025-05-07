package com.tstreet.onhand.feature.shoppinglist

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem
import com.tstreet.onhand.core.model.ui.ShoppingListUiState
import com.tstreet.onhand.core.model.ui.UiShoppingListIngredient
import com.tstreet.onhand.core.model.ui.UiShoppingListRecipe
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem.RecipeIngredientGroup
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem.StandaloneIngredient
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem.Summary
import com.tstreet.onhand.core.ui.*

@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel
) {
    val uiState by viewModel.shoppingListUiState.collectAsStateWithLifecycle()
    val errorDialogState by viewModel.errorDialogState.collectAsStateWithLifecycle()
    val removeRecipeDialogState by viewModel.removeRecipeDialogState.collectAsStateWithLifecycle()

    OnHandAlertDialog(
        onDismiss = viewModel::dismissErrorDialog,
        state = errorDialogState
    )

    // Recipe removal confirmation dialog
    OnHandAlertDialog(
        onDismiss = viewModel::dismissRemoveRecipeDialog,
        onConfirm = { },
        dismissButtonText = "Cancel",
        confirmButtonText = "Yes",
        showConfirmButton = true,
        state = removeRecipeDialogState
    )

    when (uiState) {
        ShoppingListUiState.Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        is ShoppingListUiState.Content -> {
            LazyColumn(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
                itemsIndexed((uiState as ShoppingListUiState.Content).screenContent()) { _, item ->
                    when (item) {
                        is UiShoppingListRowItem.Header -> {
                            OnHandScreenHeader(item.text)
                        }

                        is Summary -> {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = item.text,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }

                        is RecipeIngredientGroup -> {
                            RecipeIngredientGroup(
                                recipeGroup = item,
                                onMarkIngredient = viewModel::onCheckOffShoppingIngredient,
                                onUnmarkIngredient = viewModel::onUncheckShoppingIngredient,
                                onRemoveIngredient = viewModel::onRemoveIngredient,
                                onRemoveRecipe = viewModel::showRemoveRecipeDialog
                            )
                        }
                        
                        // TODO: Map standalone ingredients (i.e. no mapped recipe)
                        is StandaloneIngredient -> {
                            when {
                                item.ingredients.isNotEmpty() -> {
                                    ShoppingListIngredientCards(
                                        ingredients = item.ingredients,
                                        // TODO: potentially move viewmodel functions into mapper to reduce
                                        //  ui clutter
                                        onMarkIngredient = viewModel::onCheckOffShoppingIngredient,
                                        onUnmarkIngredient = viewModel::onUncheckShoppingIngredient,
                                        onRemoveIngredient = viewModel::onRemoveIngredient
                                    )
                                }

                                else -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 24.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = "Empty shopping cart",
                                            modifier = Modifier
                                                .size(120.dp)
                                                .align(Alignment.CenterHorizontally)
                                                .padding(32.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(16.dp),
                                            text = "Your shopping list is empty. You can add " +
                                                    "items from recipes or manually enter your " +
                                                    "own.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        ShoppingListUiState.Error -> {
            Log.d("[OnHand]", "Error loading shopping list")
        }
    }
}

@Composable
fun RecipeIngredientGroup(
    recipeGroup: RecipeIngredientGroup,
    onRemoveRecipe: () -> Unit,
    onMarkIngredient: (UiShoppingListIngredient) -> Unit,
    onUnmarkIngredient: (UiShoppingListIngredient) -> Unit,
    onRemoveIngredient: (UiShoppingListIngredient) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Recipe header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                Text(
                    text = recipeGroup.recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                IconButton(onClick = { onRemoveRecipe() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove recipe",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // Ingredients list
        ShoppingListIngredientCards(
            ingredients = recipeGroup.recipe.ingredients,
            onMarkIngredient = onMarkIngredient,
            onUnmarkIngredient = onUnmarkIngredient,
            onRemoveIngredient = onRemoveIngredient
        )
    }
}

@Composable
fun ShoppingListIngredientCards(
    ingredients: List<UiShoppingListIngredient>,
    onMarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onUnmarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onRemoveIngredient: (UiShoppingListIngredient) -> Unit = { }
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        ingredients.forEach { ingredient ->
            ShoppingListCardItem(
                ShoppingListCard(
                    ingredient = ingredient
                ),
                onMarkIngredient = onMarkIngredient,
                onUnmarkIngredient = onUnmarkIngredient,
                onRemoveIngredient = onRemoveIngredient
            )
        }
    }
}

@Composable
fun ShoppingListCardItem(
    card: ShoppingListCard,
    onMarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onUnmarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onRemoveIngredient: (UiShoppingListIngredient) -> Unit = { }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = card.ingredient.isChecked.value,
                onCheckedChange = {
                    if (card.ingredient.isChecked.value) {
                        onUnmarkIngredient(card.ingredient)
                    } else {
                        onMarkIngredient(card.ingredient)
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = card.ingredient.ingredient.name,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (card.ingredient.isChecked.value)
                        TextDecoration.LineThrough else TextDecoration.None,
                    color = if (card.ingredient.isChecked.value)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { onRemoveIngredient(card.ingredient) },
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

class ShoppingListCard(
    val ingredient: UiShoppingListIngredient
)
