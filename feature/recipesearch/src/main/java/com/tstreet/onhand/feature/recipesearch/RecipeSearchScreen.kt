package com.tstreet.onhand.feature.recipesearch

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            Surface(color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discover Recipes",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(
                        onClick = { viewModel.showInfoDialog() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                    ) {
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
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val state = uiState) {
                Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), 
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is Content -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
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
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Your pantry is empty",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Add ingredients to your pantry to see recipes that match what you have.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FilledTonalButton(
                    onClick = { /* Navigate to pantry */ },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add ingredients",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 1.dp
    ) {
        Box {
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
                    label = { 
                        Text(
                            "Sort By",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Sort dropdown"
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        containerColor = Color.Transparent
                    )
                )
                
                // Dropdown menu styling
                ExposedDropdownMenu(
                    expanded = sortSpinnerExpanded,
                    onDismissRequest = { sortSpinnerExpanded = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .animateContentSize()
                ) {
                    SortBy.values().forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    selectionOption.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selectionOption == sortOrder) 
                                            FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectionOption == sortOrder)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                sortSpinnerExpanded = false
                                onSelectionChanged(selectionOption)
                            },
                            leadingIcon = {
                                if (selectionOption == sortOrder) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}