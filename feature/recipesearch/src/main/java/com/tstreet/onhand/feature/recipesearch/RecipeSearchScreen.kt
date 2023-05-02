package com.tstreet.onhand.feature.recipesearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tstreet.onhand.core.domain.recipes.DEFAULT_SORTING
import com.tstreet.onhand.core.domain.recipes.SortBy
import com.tstreet.onhand.core.ui.*
import com.tstreet.onhand.core.ui.RecipeSaveState.*
import com.tstreet.onhand.core.ui.RecipeSearchUiState.*

@Composable
fun RecipeSearchScreen(
    navController: NavController,
    viewModel: RecipeSearchViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val openInfoDialog = remember { mutableStateOf(false) }

    if (openInfoDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openInfoDialog.value = false
            },
            title = {
                Text("Search Recipes")
            },
            text = {
                Text(
                    "Recipes shown here are based on ingredients from your pantry. By " +
                            "default we'll only show recipes where you're missing at most 3 " +
                            "ingredients."
                )
            },
            dismissButton = {
                Button(
                    onClick = {
                        openInfoDialog.value = false
                    }) {
                    Text("Got it \uD83D\uDC4C")
                }
            },
            confirmButton = { },
        )
    }

    Column(
        verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OnHandScreenHeader("Search Recipes")
            Icon(
                Icons.Default.Info,
                contentDescription = "recipe search info",
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        openInfoDialog.value = true
                    },
                tint = MaterialTheme.colorScheme.surfaceTint
            )
        }
        when (val state = uiState) {
            Loading -> {
                OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            is Success -> {
                when (state.recipes.isNotEmpty()) {
                    true -> {
                        SortBySpinner(
                            sortOrder,
                            viewModel::onSortOrderChanged
                        )
                        RecipeCardList(
                            recipes = state.recipes,
                            onItemClick = navController::navigate,
                            onSaveClick = viewModel::onRecipeSaved,
                            onUnSaveClick = viewModel::onRecipeUnsaved
                        )
                    }
                    else -> {
                        Text(
                            modifier = Modifier
                                .fillMaxSize(),
                            text = "Add ingredients to your pantry to see recipes",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            is Error -> {
                AlertDialog(
                    onDismissRequest = { },
                    title = {
                        Text("Error")
                    },
                    text = {
                        Text(state.message)
                    },
                    dismissButton = {
                        Button(onClick = { }) {
                            Text("Dismiss")
                        }
                    },
                    confirmButton = { },
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
