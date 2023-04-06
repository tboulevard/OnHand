package com.tstreet.onhand.feature.recipesearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tstreet.onhand.core.domain.DEFAULT_SORTING
import com.tstreet.onhand.core.domain.SortBy
import com.tstreet.onhand.core.ui.FullScreenErrorMessage
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.RecipeSaveState
import com.tstreet.onhand.core.ui.RecipeSaveState.*
import com.tstreet.onhand.core.ui.RecipeSearchItem
import com.tstreet.onhand.core.ui.RecipeSearchUiState.*
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN

@Composable
fun RecipeSearchScreen(
    navController: NavController,
    viewModel: RecipeSearchViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()

    Column(
        verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            Loading -> {
                OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            is Success -> {
                SortBySpinner(
                    sortOrder,
                    viewModel::onSortOrderChanged
                )
                RecipeSearchCardList(
                    recipes = state.recipes,
                    onItemClick = navController::navigate,
                    onSaveClick = viewModel::onRecipeSaved,
                    onUnSaveClick = viewModel::onRecipeUnsaved
                )
            }
            is Error -> {
                FullScreenErrorMessage(message = state.message)
            }
        }
    }
}

class RecipeSearchCard(
    val id: Int,
    val title: String,
    val usedIngredients: Int,
    val missedIngredients: Int,
    val likes: Int,
    val saveState: RecipeSaveState
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SortBySpinner(
    sortOrder: SortBy = DEFAULT_SORTING,
    onSelectionChanged: (SortBy) -> Unit = { }
) {
    var sortSpinnerExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = sortSpinnerExpanded,
            onExpandedChange = { sortSpinnerExpanded = !sortSpinnerExpanded },
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = sortOrder.toString(),
                onValueChange = { },
                label = { Text("Sort Order") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = sortSpinnerExpanded
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium
            )
            ExposedDropdownMenu(expanded = sortSpinnerExpanded, onDismissRequest = {
                sortSpinnerExpanded = false
            }) {
                SortBy.values().forEach { selectionOption ->
                    DropdownMenuItem(modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            sortSpinnerExpanded = false
                            onSelectionChanged(selectionOption)
                        },
                        text = { Text(selectionOption.toString()) })
                }
            }
        }
    }
}

@Composable
fun RecipeSearchCardList(
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
            val recipe = item.saveableRecipe.recipe
            RecipeSearchCardItem(
                card = RecipeSearchCard(
                    // TODO: clean this up
                    id = recipe.id,
                    title = recipe.title,
                    usedIngredients = recipe.usedIngredientCount,
                    missedIngredients = recipe.missedIngredientCount,
                    likes = recipe.likes,
                    saveState = item.recipeSaveState
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
fun RecipeSearchCardItem(
    @PreviewParameter(RecipeSearchCardPreviewParamProvider::class) card: RecipeSearchCard,
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
                Text(text = "${card.likes} likes", style = MaterialTheme.typography.bodySmall)
            }
            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {

                when (card.saveState) {
                    SAVED -> {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "saved",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onUnSaveClick(index) },
                            tint = MATTE_GREEN
                        )
                    }
                    NOT_SAVED -> {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "save",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onSaveClick(index) },
                        )
                    }
                    SAVING -> {
                        OnHandProgressIndicator(modifier = Modifier.size(36.dp))
                    }
                }

            }
        }
    }
}

// TODO: move all below to a better location...
class RecipeSearchCardPreviewParamProvider : PreviewParameterProvider<RecipeSearchCard> {
    override val values: Sequence<RecipeSearchCard> = sequenceOf(
        RecipeSearchCard(
            1,
            "A very long recipe name that is very long",
            10,
            3,
            100,
            NOT_SAVED
        )
    )
}
