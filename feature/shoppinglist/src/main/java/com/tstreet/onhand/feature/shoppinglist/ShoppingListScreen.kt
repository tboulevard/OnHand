package com.tstreet.onhand.feature.shoppinglist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.ui.FullScreenErrorMessage
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.ShoppingListUiState

@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel
) {
    val uiState by viewModel.shoppingListUiState.collectAsState()

    when (val state = uiState) {
        is ShoppingListUiState.Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is ShoppingListUiState.Success -> {

            when (state.ingredients.isNotEmpty()) {
                true -> {
                    ShoppingListCards(
                        state.ingredients
                    )
                }
                false -> {
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

class ShoppingListCard(
    val ingredientName: String,
    val amount: String,
    val unit: String,
    // TODO: what if two recipes request same ingredient
    val mappedRecipes: List<Recipe>,
    val index: Int
)


@Composable
fun ShoppingListCards(ingredients: List<ShoppingListIngredient>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        itemsIndexed(ingredients) { index, ingredient ->
            ShoppingListCardItem(
                ShoppingListCard(
                    ingredientName = ingredient.name,
                    amount = ingredient.amount.invoke(),
                    unit = ingredient.unit.invoke(),
                    mappedRecipes = ingredient.mappedRecipes.invoke(),
                    index = index
                )
            )
        }
    }

}

@Preview
@Composable
fun ShoppingListCardItem(
    @PreviewParameter(RecipeSearchCardPreviewParamProvider::class) card: ShoppingListCard
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp, horizontal = 16.dp
            ), shadowElevation = 8.dp, shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.clickable { /* TODO */ },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f)
            ) {
                // TODO: probably makes more sense to list amount total at top, then do breakdown for all recipes within
                Text(text = card.ingredientName, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Quantities: ${card.amount} ${card.unit}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Recipes: ${card.mappedRecipes.map { it.title }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// TODO: move all below to a better location...
class RecipeSearchCardPreviewParamProvider : PreviewParameterProvider<ShoppingListCard> {
    override val values: Sequence<ShoppingListCard> = sequenceOf(
        ShoppingListCard(
            "ingredient",
            "1.0",
            "oz",
            listOf(
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
                )
            ),
            1
        )
    )
}
