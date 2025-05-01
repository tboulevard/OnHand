package com.tstreet.onhand.feature.recipesearch

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tstreet.onhand.core.domain.usecase.recipes.DEFAULT_SORTING
import com.tstreet.onhand.core.domain.usecase.recipes.SortBy
import com.tstreet.onhand.core.model.ui.RecipeSearchUiState.*
import com.tstreet.onhand.core.ui.*

@Composable
fun RecipeSearchScreen(
    navController: NavController,
    viewModel: RecipeSearchViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val openInfoDialog = viewModel.infoDialogState.collectAsState()
    val errorDialogState = viewModel.errorDialogState.collectAsState()

    OnHandAlertDialog(
        onDismiss = viewModel::dismissInfoDialog,
        dismissButtonText = "Got it \uD83D\uDC4C",
        state = openInfoDialog.value
    )

    OnHandAlertDialog(
        onDismiss = { viewModel.dismissErrorDialog() },
        state = errorDialogState.value
    )

    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search Recipes",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(onClick = { viewModel.showInfoDialog() }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Recipe search information",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
                        SortBySpinner(
                            sortOrder = sortOrder,
                            onSelectionChanged = viewModel::onSortOrderChanged
                        )

                        RecipeCardList(
                            recipes = state.recipes,
                            onItemClick = navController::navigate,
                            onSaveClick = viewModel::onRecipeSaved,
                            onUnSaveClick = viewModel::onRecipeUnsaved,
                            onAddToShoppingListClick = viewModel::onAddToShoppingList
                        )
                    }
                }

                is Error -> {
                    OnHandAlertDialog(
                        onDismiss = { viewModel.dismissErrorDialog() },
                        state = errorDialogState.value
                    )
                }

                Empty -> EmptyStateMessage()
            }
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Add ingredients to your pantry to see recipes.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SortBySpinner(
    sortOrder: SortBy = DEFAULT_SORTING,
    onSelectionChanged: (SortBy) -> Unit = { }
) {
    var sortSpinnerExpanded by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = sortSpinnerExpanded,
                onExpandedChange = { sortSpinnerExpanded = !sortSpinnerExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    value = sortOrder.toString(),
                    onValueChange = { },
                    label = { Text("Sort By") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortSpinnerExpanded)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                ExposedDropdownMenu(
                    expanded = sortSpinnerExpanded,
                    onDismissRequest = { sortSpinnerExpanded = false }
                ) {
                    SortBy.values().forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.toString()) },
                            onClick = {
                                sortSpinnerExpanded = false
                                onSelectionChanged(selectionOption)
                            }
                        )
                    }
                }
            }
        }
    }
}
