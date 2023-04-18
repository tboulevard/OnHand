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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.ui.FullScreenErrorMessage
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.OnHandScreenHeader
import com.tstreet.onhand.core.ui.ShoppingListUiState

@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel
) {
    val uiState by viewModel.shoppingListUiState.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
        OnHandScreenHeader("Shopping List")
        when (val state = uiState) {
            is ShoppingListUiState.Loading -> {
                OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            is ShoppingListUiState.Success -> {
                when {
                    state.ingredients.isNotEmpty() -> {
                        ShoppingListCards(
                            state.ingredients,
                            viewModel::onMarkShoppingIngredient,
                            viewModel::onUnmarkShoppingIngredient
                        )
                    }
                    else -> {
                        Text(
                            modifier = Modifier
                                .fillMaxSize(),
                            text = "Shopping list empty - either because you have all ingredients " +
                                    "for all saved recipes or because you haven't saved any recipes",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            is ShoppingListUiState.Error -> {
                FullScreenErrorMessage(message = state.message)
            }
        }
    }
}

@Composable
fun ShoppingListCards(
    ingredients: List<ShoppingListIngredient>,
    onMarkIngredient: (Int) -> Unit,
    onUnmarkIngredient: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        itemsIndexed(ingredients) { index, ingredient ->
            ShoppingListCardItem(
                ShoppingListCard(
                    ingredientName = ingredient.name,
                    measures = ingredient.recipeMeasures,
                    isIngredientChecked = ingredient.isPurchased,
                    index = index
                ),
                onMarkIngredient,
                onUnmarkIngredient
            )
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
                // below line we are setting
                // the state of checkbox.
                checked = card.isIngredientChecked,
                // below line is use to add padding
                // to our checkbox.
                modifier = Modifier
                    .padding(start = 24.dp)
                    .align(Alignment.CenterVertically)
                    .size(24.dp),
                // below line is use to add on check
                // change to our checkbox.
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
                card.measures.forEach {
                    return@forEach Text(
                        modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp),
                        text = "${it.amount} ${it.unit} for: ${it.recipe.title}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

class ShoppingListCard(
    val ingredientName: String,
    val measures: List<RecipeMeasure>,
    val isIngredientChecked: Boolean,
    val index: Int
)

// TODO: move all below to a better location...
class RecipeSearchCardPreviewParamProvider : PreviewParameterProvider<ShoppingListCard> {
    override val values: Sequence<ShoppingListCard> = sequenceOf(
        ShoppingListCard(
            ingredientName = "ingredient",
            measures = listOf(
                RecipeMeasure(
                    recipe = Recipe(
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
                    amount = 10.5,
                    unit = "cups"
                ),
                RecipeMeasure(
                    recipe = Recipe(
                        id = 2,
                        title = "Recipe title2",
                        image = "image",
                        imageType = "imageType",
                        missedIngredientCount = 2,
                        missedIngredients = listOf(
                            RecipeIngredient(
                                Ingredient(40, "tomato"),
                                amount = 2.0,
                                unit = "oz"
                            )
                        ),
                        usedIngredientCount = 1,
                        usedIngredients = listOf(
                            RecipeIngredient(
                                Ingredient(50, "onion"),
                                amount = 1.0,
                                unit = "unit"
                            )
                        ),
                        likes = 100
                    ),
                    amount = 2.0,
                    unit = "oz"
                )
            ),
            index = 0,
            isIngredientChecked = true
        )
    )
}
