package com.tstreet.onhand.feature.savedrecipes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.common.R.string.nothing_saved
import com.tstreet.onhand.core.common.R.string.saved_recipes
import com.tstreet.onhand.core.model.ui.SavedRecipesUiState
import com.tstreet.onhand.core.model.ui.SavedRecipesUiState.Content
import com.tstreet.onhand.core.model.ui.SavedRecipesUiState.Loading
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

    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(saved_recipes),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is Content -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        RecipeCardList(
                            recipes = state.recipes,
                            onItemClick = navController::navigate,
                            onSaveClick = viewModel::onRecipeSaved,
                            onUnSaveClick = viewModel::onRecipeUnsaved,
                            onAddToShoppingListClick = viewModel::onAddToShoppingList
                        )
                    }
                }

                is SavedRecipesUiState.Error -> {
                    OnHandAlertDialog(
                        onDismiss = { viewModel.dismissErrorDialog() },
                        state = errorDialogState
                    )
                }

                SavedRecipesUiState.Empty -> EmptyStateMessage()
            }
        }
    }
}

@Composable
private fun EmptyStateMessage() {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp),
            text = stringResource(nothing_saved),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}