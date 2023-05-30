package com.tstreet.onhand.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient

sealed class ShoppingListUiState(
    val ingredients: List<ShoppingListIngredient>,
    val recipes: List<Recipe>
) {
    object Loading : ShoppingListUiState(emptyList(), emptyList())

    // TODO: refactor duplicate screen content functions...
    data class Success(
        val shoppingListIngredients: List<ShoppingListIngredient>,
        val mappedRecipes: List<Recipe>
    ) : ShoppingListUiState(shoppingListIngredients, mappedRecipes)

    data class Error(
        val message: String,
        val shoppingListIngredients: List<ShoppingListIngredient>,
        val mappedRecipes: List<Recipe>
    ) : ShoppingListUiState(shoppingListIngredients, mappedRecipes)

    fun screenContent() = listOf(
        ShoppingListItem.Header(
            text = "Shopping List"
        ),
        ShoppingListItem.Summary(
            numberOfRecipes = recipes.size,
            numberOfIngredients = ingredients.size
        ),
        ShoppingListItem.MappedRecipes(
            recipes = recipes
        ),
        ShoppingListItem.Ingredients(
            ingredients = ingredients
        )
    )
}

sealed interface ShoppingListItem {

    data class Header(
        val text: String
    ) : ShoppingListItem

    data class Summary(
        private val numberOfRecipes: Int,
        private val numberOfIngredients: Int
    ) : ShoppingListItem {

        val text = "$numberOfRecipes recipes - $numberOfIngredients items"
    }

    data class MappedRecipes(
        val recipes: List<Recipe>
    ) : ShoppingListItem

    data class Ingredients(
        val ingredients: List<ShoppingListIngredient>
    ) : ShoppingListItem
}

@Composable
fun ShoppingListRecipeCards(
    recipes: List<Recipe>,
    onItemClick: (String) -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO: Implement item keys for this approach to avoid recompositions, currently
        //  doesn't appear to work...
        itemsIndexed(recipes, key = { _, item -> item.id }) { index, recipe ->
            ShoppingListRecipeItem(
                ShoppingListRecipeCard(
                    recipe = recipe,
                    onItemClick = onItemClick,
                    onRemoveClick = onRemoveClick,
                    index
                )
            )
        }
    }
}

@Preview
@Composable
fun ShoppingListRecipeItem(
    @PreviewParameter(ShoppingListRecipeCardPreviewParamProvider::class)
    card: ShoppingListRecipeCard
) {
    Surface(
        modifier = Modifier
            .size(148.dp)
            .padding(8.dp),
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = card.recipe.image,
                    contentDescription = null
                )
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "clear ingredients",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .clickable { card.onRemoveClick(card.index) },
                    tint = MaterialTheme.colorScheme.inverseSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { card.onItemClick("recipe_detail/${card.recipe.id}") }
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = card.recipe.title,
                        modifier = Modifier
                            .padding(4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

class ShoppingListRecipeCard(
    val recipe: Recipe,
    val onItemClick: (String) -> Unit,
    val onRemoveClick: (Int) -> Unit,
    val index: Int
)

// TODO: move below to a better location
class ShoppingListRecipeCardPreviewParamProvider :
    PreviewParameterProvider<ShoppingListRecipeCard> {
    override val values: Sequence<ShoppingListRecipeCard> = sequenceOf(
        ShoppingListRecipeCard(
            recipe = Recipe(
                id = 1,
                title = "A very long recipe name that is very long",
                image = "image.jpg",
                imageType = "jpeg",
                usedIngredientCount = 10,
                usedIngredients = emptyList(),
                missedIngredientCount = 3,
                missedIngredients = emptyList(),
                likes = 100,
                isCustom = true
            ),
            onItemClick = { },
            onRemoveClick = { },
            index = 0
        )
    )
}
