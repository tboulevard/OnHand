package com.tstreet.onhand.core.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.model.ShoppingListIngredient

sealed interface ShoppingListUiState {

    object Loading : ShoppingListUiState

    data class Success(
        val ingredients: List<ShoppingListIngredient>,
        val recipes: List<Recipe>
    ) : ShoppingListUiState {

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

    data class Error(
        val message: String
    ) : ShoppingListUiState
}

sealed class ShoppingListItem {

    data class Header(
        val text: String
    ) : ShoppingListItem()

    data class Summary(
        private val numberOfRecipes: Int,
        private val numberOfIngredients: Int
    ) : ShoppingListItem() {

        val text = "$numberOfRecipes recipes - $numberOfIngredients items"
    }

    data class MappedRecipes(
        val recipes: List<Recipe>
    ) : ShoppingListItem()

    data class Ingredients(
        val ingredients: List<ShoppingListIngredient>
    ) : ShoppingListItem()
}

@Composable
fun ShoppingListRecipeCards(
    recipes: List<Recipe>,
    onItemClick: (String) -> Unit = { },
    onRemoveClick: () -> Unit = { },
    onConfirmRemoveClick: (Int) -> Unit,
    onDismissDialog: () -> Unit,
    removeRecipeConfirmationDialogState: Boolean,

    ) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(recipes) { index, recipe ->
            OnHandAlertDialog(
                onDismiss = onDismissDialog,
                onConfirm = { onConfirmRemoveClick(index) },
                titleText = "Are you sure?",
                bodyText = "Are you sure you'd like to remove this recipe and all its " +
                        "ingredients from your shopping list?",
                dismissButtonText = "Cancel",
                confirmButtonText = "Yes",
                showConfirmButton = true,
                shouldDisplay = removeRecipeConfirmationDialogState
            )

            ShoppingListRecipeCardItem(
                recipe = recipe,
                onItemClick = onItemClick,
                onRemoveClick = onRemoveClick
            )
        }
    }
}

@Preview
@Composable
fun ShoppingListRecipeCardItem(
    @PreviewParameter(RecipeCardShoppingListPreviewParamProvider::class) recipe: Recipe,
    index: Int = 0,
    onItemClick: (String) -> Unit = { },
    onRemoveClick: () -> Unit = { }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.recipe_placeholder),
                    contentDescription = "recipe image",
                    modifier = Modifier.size(width = 192.dp, height = 148.dp)
                )
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "clear ingredients",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .clickable { onRemoveClick() },
                    tint = MaterialTheme.colorScheme.inverseOnSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick("recipe_detail/${recipe.id}") }
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = recipe.title,
                        modifier = Modifier
                            .padding(4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// TODO: move below to a better location
class RecipeCardShoppingListPreviewParamProvider : PreviewParameterProvider<Recipe> {
    override val values: Sequence<Recipe> = sequenceOf(
        Recipe(
            id = 1,
            title = "A very long recipe name that is very long",
            image = "image",
            imageType = "imageType",
            usedIngredientCount = 10,
            usedIngredients = emptyList(),
            missedIngredientCount = 3,
            missedIngredients = emptyList(),
            likes = 100
        )
    )
}
