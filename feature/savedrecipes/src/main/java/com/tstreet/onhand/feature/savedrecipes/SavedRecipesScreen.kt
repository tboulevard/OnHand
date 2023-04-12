package com.tstreet.onhand.feature.savedrecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.model.*
import com.tstreet.onhand.core.ui.*
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN
import kotlin.reflect.KFunction1

@Composable
fun SavedRecipesScreen(
    navController: NavHostController,
    viewModel: SavedRecipesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is SavedRecipesUiState.Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is SavedRecipesUiState.Success -> {
            when (state.recipes.isNotEmpty()) {
                true -> {
                    SavedRecipeCards(
                        state.recipes,
                        onItemClick = navController::navigate,
                        onSaveClick = viewModel::onRecipeSaved,
                        onUnSaveClick = viewModel::onRecipeUnsaved
                    )
                }
                false -> {
                    Text(
                        modifier = Modifier
                            .fillMaxSize(),
                        text = "No saved recipes (yet)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        is SavedRecipesUiState.Error -> {
            FullScreenErrorMessage(message = state.message)
        }
    }
}

class SavedRecipeCard(
    val id: Int,
    val title: String,
    val usedIngredients: Int,
    val missedIngredients: Int,
    val likes: Int,
    val saveState: RecipeSaveState
)

@Composable
fun SavedRecipeCards(
    recipes: List<RecipeSearchItem>,
    onItemClick: (String) -> Unit,
    onSaveClick: (Int) -> Unit,
    onUnSaveClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        itemsIndexed(recipes) { index, item ->
            SavedRecipeCardItem(
                SavedRecipeCard(
                    id = item.recipe.id,
                    title = item.recipe.title,
                    usedIngredients = item.recipe.usedIngredientCount,
                    missedIngredients = item.recipe.missedIngredientCount,
                    likes = item.recipe.likes,
                    saveState = RecipeSaveState.SAVED
                ),
                onItemClick = onItemClick,
                onSaveClick = onSaveClick,
                onUnSaveClick = onUnSaveClick
            )
        }
    }
}

@Preview
@Composable
fun SavedRecipeCardItem(
    @PreviewParameter(SavedRecipeCardPreviewParamProvider::class) card: SavedRecipeCard,
    index: Int = 0,
    onItemClick: (String) -> Unit = { },
    onSaveClick: (Int) -> Unit = { },
    onUnSaveClick: (Int) -> Unit = { }
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp, horizontal = 16.dp
            ), shadowElevation = 8.dp, shape = MaterialTheme.shapes.medium
    ) {
        Row(
            // TODO: pretty unclear we are passing in a function to navigate here, might be worth
            // refactoring later
            // TODO: hardcoding recipe_detail route not ideal. Not easy to fix b/c it would require
            // this module to rely on :app, refactor later...
            modifier = Modifier.clickable { onItemClick("recipe_detail/${card.id}") },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f)
            ) {
                Text(text = card.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "${card.usedIngredients} ingredients used",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${card.missedIngredients} ingredients missed",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${card.likes} likes",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {

                when (card.saveState) {
                    RecipeSaveState.SAVED -> {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "saved",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onUnSaveClick(index) },
                            tint = MATTE_GREEN
                        )
                    }
                    RecipeSaveState.NOT_SAVED -> {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "save",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onSaveClick(index) },
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

// TODO: move all below to a better location...
class SavedRecipeCardPreviewParamProvider : PreviewParameterProvider<SavedRecipeCard> {
    override val values: Sequence<SavedRecipeCard> = sequenceOf(
        SavedRecipeCard(
            id = 1,
            title = "title",
            usedIngredients = 1,
            missedIngredients = 2,
            likes = 10,
            saveState = RecipeSaveState.SAVED
        )
    )
}
