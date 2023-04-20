package com.tstreet.onhand.feature.savedrecipes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.ui.*

@Composable
fun SavedRecipesScreen(
    navController: NavHostController,
    viewModel: SavedRecipesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()
    ) {
        OnHandScreenHeader("Saved Recipes")
        when (val state = uiState) {
            is SavedRecipesUiState.Loading -> {
                OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            is SavedRecipesUiState.Success -> {
                when (state.recipes.isNotEmpty()) {
                    true -> {
                        RecipeCardList(
                            recipes = state.recipes,
                            onItemClick = navController::navigate,
                            onSaveClick = viewModel::onRecipeSaved,
                            onUnSaveClick = viewModel::onRecipeUnsaved
                        )
                    }
                    false -> {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(8.dp),
                                text = "Nothing saved \uD83E\uDEE0\n\n P.S.: We use these to " +
                                        "curate your shopping list \uD83E\uDD0C",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
            is SavedRecipesUiState.Error -> {
                FullScreenErrorMessage(message = state.message)
            }
        }
    }
}