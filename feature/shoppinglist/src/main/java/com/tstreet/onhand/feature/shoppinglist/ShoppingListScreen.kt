package com.tstreet.onhand.feature.shoppinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
    val removeRecipeConfirmationDialogState by viewModel.removeRecipeConfirmationDialogState.collectAsStateWithLifecycle()

    // For general errors
    OnHandAlertDialog(
        onDismiss = viewModel::dismissErrorDialog,
        titleText = "Error",
        bodyText = errorDialogState.message,
        shouldDisplay = errorDialogState.shouldDisplay
    )

    when (val state = uiState) {
        is ShoppingListUiState.Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is ShoppingListUiState.Success -> {
            LazyColumn(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
                itemsIndexed(state.screenContent) { index, item ->
                    when (item) {
                        is ShoppingListItem.Header -> {
                            OnHandScreenHeader("Shopping List")
                        }
                        is ShoppingListItem.Summary -> {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = "${state.recipes.size} recipes - ${state.ingredients.size} items",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                        is ShoppingListItem.Ingredients -> {
                            state.ingredients.map {
                                ShoppingListCardItem(
                                    ShoppingListCard(
                                        ingredientName = it.name,
                                        recipe = it.mappedRecipe,
                                        isIngredientChecked = it.isPurchased,
                                        index = index
                                    ),
                                    viewModel::onCheckOffShoppingIngredient,
                                    viewModel::onUncheckShoppingIngredient
                                )
                            }
                            // TODO: rework - will be repeatedly drawn for every item in list (but
                            //  itll be empty so does it matter?)
//                                else -> {
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxSize(),
//                                        horizontalArrangement = Arrangement.Center
//                                    ) {
//                                        Text(
//                                            modifier = Modifier
//                                                .align(Alignment.CenterVertically)
//                                                .padding(8.dp),
//                                            text = "Your shopping list is empty.",
//                                            style = MaterialTheme.typography.titleLarge,
//                                            textAlign = TextAlign.Center,
//                                        )
//                                    }
//                                }
//                            }
                        }
                        is ShoppingListItem.MappedIngredientsHeader -> {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = "Ingredients from Recipes",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                        is ShoppingListItem.MappedRecipes -> {
                            when {
                                item.recipes.isNotEmpty() -> {
                                    ShoppingListRecipeCards(
                                        recipes = state.recipes,
                                        onItemClick = { /* TODO */ },
                                        onRemoveClick = viewModel::showRemoveRecipeConfirmationDialog,
                                        onConfirmRemoveClick = viewModel::onRemoveRecipe,
                                        onDismissDialog = viewModel::dismissRemoveRecipeConfirmationDialog,
                                        removeRecipeConfirmationDialogState = removeRecipeConfirmationDialogState
                                    )
                                }
                                else -> { /* TODO, no recipes to show */
                                }
                            }
                        }
                    }
                }
            }
        }
        is ShoppingListUiState.Error -> {
            // For errors retrieving shopping list itself
            FullScreenErrorMessage(message = state.message)
        }
    }
}

@Preview
@Composable
fun ShoppingListCardItem(
    @PreviewParameter(RecipeSearchCardPreviewParamProvider::class) card: ShoppingListCard,
    onMarkIngredient: (Int) -> Unit = { },
    onUnmarkIngredient: (Int) -> Unit = { }
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
                likes = 100
            ),
            index = 0,
            isIngredientChecked = true
        )
    )
}
