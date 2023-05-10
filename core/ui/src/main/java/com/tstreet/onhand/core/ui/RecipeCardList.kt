package com.tstreet.onhand.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.Recipe
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN

@Composable
fun RecipeCardList(
    recipes: List<RecipeWithSaveState>,
    onItemClick: (String) -> Unit,
    onSaveClick: (Int) -> Unit,
    onUnSaveClick: (Int) -> Unit,
    onAddToShoppingListClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        itemsIndexed(recipes) { index, item ->
            val recipe = item.recipe
            RecipeCardItem(
                recipeWithSaveState = RecipeWithSaveState(
                    Recipe(
                        id = recipe.id,
                        title = recipe.title,
                        image = recipe.image,
                        imageType = recipe.imageType,
                        usedIngredientCount = recipe.usedIngredientCount,
                        usedIngredients = recipe.usedIngredients,
                        missedIngredientCount = recipe.missedIngredientCount,
                        missedIngredients = recipe.missedIngredients,
                        likes = recipe.likes
                    ),
                    recipeSaveState = item.recipeSaveState

                ),
                index = index,
                onItemClick = onItemClick,
                onSaveClick = onSaveClick,
                onUnSaveClick = onUnSaveClick,
                onAddToShoppingListClick = onAddToShoppingListClick
            )
        }
    }
}

@Preview
@Composable
fun RecipeCardItem(
    @PreviewParameter(RecipeCardPreviewParamProvider::class) recipeWithSaveState: RecipeWithSaveState,
    index: Int = 0,
    onItemClick: (String) -> Unit = { },
    onSaveClick: (Int) -> Unit = { },
    onUnSaveClick: (Int) -> Unit = { },
    onAddToShoppingListClick: (Int) -> Unit = { }
) {
    val recipe = recipeWithSaveState.recipe

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp, horizontal = 16.dp
            ), shadowElevation = 8.dp, shape = MaterialTheme.shapes.medium,
        color = if (recipeWithSaveState.recipeSaveState == RecipeSaveState.SAVED) {
            MATTE_GREEN
        } else {
            MaterialTheme.colorScheme.surfaceTint
        }
    ) {
        Row(
            modifier = Modifier.clickable { onItemClick("recipe_detail/${recipe.id}") },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Leftmost column, containing card information
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = recipe.title,
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "${if (recipe.usedIngredientCount > 1) "${recipe.usedIngredientCount} ingredients" else "1 ingredient"} used",
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (recipe.missedIngredientCount > 0) {
                    Text(
                        text = "${if (recipe.missedIngredientCount > 1) "${recipe.missedIngredientCount} ingredients" else "1 ingredient"} missing",
                        modifier = Modifier.padding(4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(modifier = Modifier.padding(4.dp)) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "likes",
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.inverseOnSurface
                    )
                    Text(
                        text = "${recipe.likes} likes",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Add to shopping list button
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { onAddToShoppingListClick(index) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                            contentColor = MaterialTheme.colorScheme.inverseSurface
                        ),
                        enabled = recipe.missedIngredientCount > 0
                    ) {
                        if (recipe.missedIngredientCount > 0) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "shopping_cart",
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 4.dp)
                            )
                            Text("Add ${if (recipe.missedIngredientCount > 1) "${recipe.missedIngredientCount} missing ingredients" else "1 missing ingredient"}")
                        } else {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "check",
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 4.dp)
                            )
                            Text("You have all ingredients")
                        }
                    }
                }
            }

            // Rightmost column, containing card information
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp)
            ) {

                when (recipeWithSaveState.recipeSaveState) {
                    RecipeSaveState.SAVED -> {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "saved",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onUnSaveClick(index) },
                            tint = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                    RecipeSaveState.NOT_SAVED -> {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "save",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onSaveClick(index) },
                            tint = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                    RecipeSaveState.SAVING -> {
                        OnHandProgressIndicator(modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingListRecipeCards(
    recipes: List<Recipe>,
    onItemClick: (String) -> Unit = { },
    onRemoveFromShoppingList: (Int) -> Unit = { }
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(recipes) { index, recipe ->
            ShoppingListRecipeCardItem(
                recipe = recipe,
                onItemClick = onItemClick,
                onRemoveFromShoppingList = onRemoveFromShoppingList
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
    onRemoveFromShoppingList: (Int) -> Unit = { }
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
                        .clickable { onRemoveFromShoppingList(index) },
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

// Recipe wrapped in save state to allow the view model to toggle it
data class RecipeWithSaveState(
    val recipe: Recipe,
    val recipeSaveState: RecipeSaveState
)

// TODO: move all below to a better location...
class RecipeCardPreviewParamProvider : PreviewParameterProvider<RecipeWithSaveState> {
    override val values: Sequence<RecipeWithSaveState> = sequenceOf(
        RecipeWithSaveState(
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
            ),
            recipeSaveState = RecipeSaveState.SAVED
        )
    )
}

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
