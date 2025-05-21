package com.tstreet.onhand.feature.ingredientsearch

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.common.R.string.add_ingredient
import com.tstreet.onhand.core.common.R.string.error_message
import com.tstreet.onhand.core.common.R.string.in_pantry
import com.tstreet.onhand.core.common.R.string.no_results_found
import com.tstreet.onhand.core.common.R.string.remove_ingredient
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.ui.IngredientSearchBar
import com.tstreet.onhand.core.ui.OnHandAlertDialog
import com.tstreet.onhand.core.ui.OnHandProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSearchScreen(
    viewModel: IngredientSearchViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val searchText by viewModel.displayedSearchText.collectAsState()
    val errorDialogState = viewModel.errorDialogState.collectAsState()

    val onIngredientSearchTextChanged = remember { viewModel::onSearchTextChanged }
    val dismissErrorDialog = remember { viewModel::dismissErrorDialog }

    OnHandAlertDialog(
        onDismiss = dismissErrorDialog,
        state = errorDialogState.value
    )

    Scaffold(
        topBar = {
            IngredientSearchBar(
                searchText = searchText,
                onTextChanged = onIngredientSearchTextChanged,
                onBackClicked = onBackClicked
            )
        }
    ) { paddingValues ->
        IngredientSearchCardList(
            modifier = Modifier.padding(paddingValues),
            searchUiState = uiState,
            onItemClick = { ingredient ->
                viewModel.onItemClick(ingredient)
            }
        )
    }
}

private class IngredientSearchCard(
    val name: String,
    val isSelected: Boolean,
    val inPantry: Boolean
)

@Composable
private fun IngredientSearchListItem(
    card: IngredientSearchCard,
    ingredient: UiSearchIngredient,
    onItemClicked: (UiSearchIngredient) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = { onItemClicked(ingredient) }),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (card.isSelected) MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (card.isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                           else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (card.inPantry) {
                    Text(
                        text = stringResource(in_pantry),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (card.isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = if (card.isSelected) Icons.Outlined.Check else Icons.Outlined.Add,
                contentDescription = if (card.isSelected) 
                    stringResource(remove_ingredient)
                else 
                    stringResource(add_ingredient),
                tint = if (card.isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                      else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun IngredientSearchCardList(
    modifier: Modifier = Modifier,
    searchUiState: SearchUiState,
    onItemClick: (UiSearchIngredient) -> Unit
) {
    Log.d("[OnHand]", "IngredientSearchCardList recomposition")

    Box(modifier = modifier.fillMaxSize()) {
        when (searchUiState) {
            is SearchUiState.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(no_results_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is SearchUiState.Loading -> {
                OnHandProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is SearchUiState.Content -> {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        key = { _, item -> item.ingredient.id },
                        items = searchUiState.ingredients
                    ) { _, item ->
                        IngredientSearchListItem(
                            card = IngredientSearchCard(
                                name = item.ingredient.name,
                                isSelected = item.isSelected.value,
                                inPantry = item.inPantry.value
                            ),
                            item,
                            onItemClicked = onItemClick
                        )
                    }
                }
            }

            is SearchUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(error_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}