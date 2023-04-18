package com.tstreet.onhand.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onUnSaveClick: (Int) -> Unit
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
                onUnSaveClick = onUnSaveClick
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
    onUnSaveClick: (Int) -> Unit = { }
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
            // TODO: hardcoding recipe_detail route not ideal. Not easy to fix b/c it would require
            // this module to rely on :app, refactor later...
            modifier = Modifier.clickable { onItemClick("recipe_detail/${recipe.id}") },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
                when (recipe.missedIngredientCount > 0) {
                    true -> {
                        val usedIngredientString =
                            if (recipe.usedIngredientCount > 1) "${recipe.usedIngredientCount} ingredients" else "1 ingredient"
                        val missedIngredientString =
                            if (recipe.missedIngredientCount > 1) "${recipe.missedIngredientCount} ingredients" else "1 ingredient"

                        Text(
                            text = "$usedIngredientString used",
                            modifier = Modifier.padding(4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "$missedIngredientString missing",
                            modifier = Modifier.padding(4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {
                        val correctedSuffix =
                            if (recipe.usedIngredientCount > 1) "all ${recipe.usedIngredientCount} ingredients" else "the 1 ingredient"

                        Row(modifier = Modifier.padding(4.dp)) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "ingredients used",
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 4.dp),
                                tint = MaterialTheme.colorScheme.inverseOnSurface
                            )
                            Text(
                                text = "You have $correctedSuffix",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
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
// TODO: where to put time if we decide to send through later
//                Row(modifier = Modifier.padding(4.dp)) {
//                    Icon(
//                        painterResource(com.tstreet.onhand.core.ui.R.drawable.timer),
//                        contentDescription = "time",
//                        modifier = Modifier
//                            .size(18.dp)
//                            .padding(end = 4.dp),
//                        tint = MaterialTheme.colorScheme.inverseOnSurface
//                    )
//                    Text(
//                        text = "-- minutes",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }

            }
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
