package com.tstreet.onhand.feature.shoppinglist

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
        onConfirm = viewModel::onRemoveRecipe,
        dismissButtonText = "Cancel",
        confirmButtonText = "Yes",
        showConfirmButton = true,
        state = removeRecipeDialogState
    )

    when (val state = uiState) {
        is ShoppingListUiState.Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is ShoppingListUiState.Success -> {
            LazyColumn(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
                // TODO: specify contentType here, since rows are different
                //  https://developer.android.com/jetpack/compose/lists#content-type
                // TODO: Implement item keys for this approach to avoid recompositions, hashCode
                //  doesn't appear to work...
                itemsIndexed(state.screenContent()) { _, item ->
                    when (item) {
                        is ShoppingListItem.Header -> {
                            OnHandScreenHeader(item.text)
                        }
                        is ShoppingListItem.Summary -> {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = item.text,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                        is ShoppingListItem.MappedRecipes -> {
                            ShoppingListRecipeCards(
                                recipes = item.recipes,
                                onItemClick = { /* TODO */ },
                                onRemoveClick = viewModel::showRemoveRecipeDialog
                            )
                        }
                        is ShoppingListItem.Ingredients -> {
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
        is ShoppingListUiState.Error -> {
            LazyColumn(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
                // TODO: specify contentType here, since rows are different
                //  https://developer.android.com/jetpack/compose/lists#content-type
                itemsIndexed(state.screenContent()) { _, item ->
                    when (item) {
                        is ShoppingListItem.Header -> {
                            OnHandScreenHeader(item.text)
                        }
                        is ShoppingListItem.Summary -> {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = item.text,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                        is ShoppingListItem.MappedRecipes -> {
                            ShoppingListRecipeCards(
                                recipes = item.recipes,
                                onItemClick = { /* TODO */ },
                                onRemoveClick = viewModel::showRemoveRecipeDialog
                            )
                        }
                        is ShoppingListItem.Ingredients -> {
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
    }
}

@Composable
fun ShoppingListIngredientCards(
    ingredients: List<ShoppingListIngredient>,
    onMarkIngredient: (Int) -> Unit = { },
    onUnmarkIngredient: (Int) -> Unit = { },
    onRemoveIngredient: (Int) -> Unit = { }
) {
    ingredients.mapIndexed { index, ingredient ->
        ShoppingListCardItem(
            ShoppingListCard(
                ingredientName = ingredient.name,
                recipe = ingredient.mappedRecipe,
                isIngredientChecked = ingredient.isPurchased,
                index = index
            ),
            onMarkIngredient = onMarkIngredient,
            onUnmarkIngredient = onUnmarkIngredient,
            onRemoveIngredient = onRemoveIngredient
        )
    }
}

@Preview
@Composable
fun ShoppingListCardItem(
    @PreviewParameter(RecipeSearchCardPreviewParamProvider::class) card: ShoppingListCard,
    onMarkIngredient: (Int) -> Unit = { },
    onUnmarkIngredient: (Int) -> Unit = { },
    onRemoveIngredient: (Int) -> Unit = { }
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
                checked = card.isIngredientChecked,
                modifier = Modifier
                    .padding(start = 24.dp)
                    .align(Alignment.CenterVertically)
                    .size(24.dp),
                onCheckedChange = {
                    if (card.isIngredientChecked) {
                        onUnmarkIngredient(card.index)
                    } else {
                        onMarkIngredient(card.index)
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
                    text = card.ingredientName,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (card.recipe != null) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = card.recipe.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Icon(
                Icons.Default.Clear,
                contentDescription = "remove",
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onRemoveIngredient(card.index) },
                tint = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }
}

class ShoppingListCard(
    val ingredientName: String,
    val recipe: Recipe?,
    val isIngredientChecked: Boolean,
    val index: Int
)

// TODO: move all below to a better location...
class RecipeSearchCardPreviewParamProvider : PreviewParameterProvider<ShoppingListCard> {
    override val values: Sequence<ShoppingListCard> = sequenceOf(
        ShoppingListCard(
            ingredientName = "ingredient",
            recipe =
            Recipe(
                id = 1,
                title = "Recipe title1",
                image = "image",
                imageType = "imageType",
                missedIngredientCount = 2,
                missedIngredients = listOf(
                    RecipeIngredient(
                        Ingredient(4, "cheese"),
                        amount = 2.0,
                        unit = "oz"
                    )
                ),
                usedIngredientCount = 1,
                usedIngredients = listOf(
                    RecipeIngredient(
                        Ingredient(5, "garlic"),
                        amount = 1.0,
                        unit = "clove"
                    )
                ),
                likes = 100,
                isCustom = true,
            ),
            index = 0,
            isIngredientChecked = true
        )
    )
}
