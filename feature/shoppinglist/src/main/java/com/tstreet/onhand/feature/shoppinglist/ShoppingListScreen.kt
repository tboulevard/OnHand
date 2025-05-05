package com.tstreet.onhand.feature.shoppinglist

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.data.RecipeIngredient
import com.tstreet.onhand.core.model.ui.UiShoppingListRowItem
import com.tstreet.onhand.core.model.ui.ShoppingListUiState
import com.tstreet.onhand.core.model.ui.UiShoppingListIngredient
import com.tstreet.onhand.core.model.ui.UiShoppingListRecipe
import com.tstreet.onhand.core.ui.*

@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel
) {
    val uiState by viewModel.shoppingListUiState.collectAsStateWithLifecycle()
    val errorDialogState by viewModel.errorDialogState.collectAsStateWithLifecycle()
    val removeRecipeDialogState by viewModel.removeRecipeDialogState.collectAsStateWithLifecycle()

    // For general errors
    OnHandAlertDialog(
        onDismiss = viewModel::dismissErrorDialog,
        state = errorDialogState
    )

    // Recipe removal confirmation dialog
    OnHandAlertDialog(
        onDismiss = viewModel::dismissRemoveRecipeDialog,
        onConfirm = {   },
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
                // TODO: specify contentType here, since rows are different
                //  https://developer.android.com/jetpack/compose/lists#content-type
                itemsIndexed((uiState as ShoppingListUiState.Content).screenContent()) { _, item ->
                    when (item) {
                        is UiShoppingListRowItem.Header -> {
                            OnHandScreenHeader(item.text)
                        }
                        is UiShoppingListRowItem.Summary -> {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = item.text,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                        is UiShoppingListRowItem.MappedRecipes -> {
                            ShoppingListRecipeCards(
                                recipePreviews = item.recipePreviews,
                                onItemClick = { /* TODO */ },
                                onRemoveClick = viewModel::showRemoveRecipeDialog
                            )
                        }
                        is UiShoppingListRowItem.Ingredients -> {
                            when {
                                item.ingredients.isNotEmpty() -> {
                                    ShoppingListIngredientCards(
                                        ingredients = item.ingredients,
                                        onMarkIngredient = viewModel::onCheckOffShoppingIngredient,
                                        onUnmarkIngredient = viewModel::onUncheckShoppingIngredient,
                                        onRemoveIngredient = viewModel::onRemoveIngredient
                                    )
                                }
                                else -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = "empty shopping card",
                                            modifier = Modifier
                                                .size(120.dp)
                                                .align(Alignment.CenterHorizontally)
                                                .padding(32.dp),
                                            tint = MaterialTheme.colorScheme.inverseOnSurface
                                        )
                                        Text(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(16.dp),
                                            text = "Your shopping list is empty. You can add " +
                                                    "items from recipes or manually enter your " +
                                                    "own.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
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
fun ShoppingListIngredientCards(
    ingredients: List<UiShoppingListIngredient>,
    onMarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onUnmarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onRemoveIngredient: (UiShoppingListIngredient) -> Unit = { }
) {
    ingredients.mapIndexed { _, ingredient ->
        ShoppingListCardItem(
            ShoppingListCard(
                ingredient = ingredient,
                recipePreview = ingredient.mappedRecipe
            ),
            onMarkIngredient = onMarkIngredient,
            onUnmarkIngredient = onUnmarkIngredient,
            onRemoveIngredient = onRemoveIngredient
        )
    }
}

@Composable
fun ShoppingListCardItem(
    card: ShoppingListCard,
    onMarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onUnmarkIngredient: (UiShoppingListIngredient) -> Unit = { },
    onRemoveIngredient: (UiShoppingListIngredient) -> Unit = { }
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp, horizontal = 16.dp
            ),
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceTint
    ) {
        Row(
            modifier = Modifier.clickable { /* TODO */ },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = card.ingredient.isChecked.value,
                modifier = Modifier
                    .padding(start = 24.dp)
                    .align(Alignment.CenterVertically)
                    .size(24.dp),
                onCheckedChange = {
                    if (card.ingredient.isChecked.value) {
                        onUnmarkIngredient(card.ingredient)
                    } else {
                        onMarkIngredient(card.ingredient)
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkmarkColor = MaterialTheme.colorScheme.inverseOnSurface,
                    checkedColor = MaterialTheme.colorScheme.inverseSurface,
                    uncheckedColor = MaterialTheme.colorScheme.inverseSurface
                )
            )
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = card.ingredient.ingredient.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (card.recipePreview != null) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = card.recipePreview.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Icon(
                Icons.Default.Clear,
                contentDescription = "remove",
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onRemoveIngredient(card.ingredient) },
                tint = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }
}

class ShoppingListCard(
    val ingredient: UiShoppingListIngredient,
    val recipePreview: UiShoppingListRecipe?
)
