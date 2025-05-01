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
import com.tstreet.onhand.core.model.ui.SavedRecipesUiState
import com.tstreet.onhand.core.ui.*

@Composable
fun SavedRecipesScreen(
    navController: NavHostController,
    viewModel: SavedRecipesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorDialogState by viewModel.errorDialogState.collectAsState()

    OnHandAlertDialog(
        onDismiss = { viewModel.dismissErrorDialog() },
        state = errorDialogState
    )

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
                            onSaveClick = { viewModel::onRecipeSaved },
                            onUnSaveClick = { viewModel::onRecipeUnsaved },
                            onAddToShoppingListClick = viewModel::onAddToShoppingList
                        )
                    }
                    false -> {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(8.dp),
                                text = "Nothing saved \uD83E\uDEE0",
                                style = MaterialTheme.typography.bodyMedium,
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
