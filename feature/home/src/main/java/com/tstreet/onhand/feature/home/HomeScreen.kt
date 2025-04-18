package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.ui.IngredientSearchBar
import com.tstreet.onhand.core.ui.OnHandAlertDialog
import com.tstreet.onhand.core.ui.OnHandProgressIndicator

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onIngredientSearchClick: () -> Unit
) {
    Log.d("[OnHand]", "HomeScreen recomposition")
    val pantryUiState by viewModel.pantryUiState.collectAsStateWithLifecycle()
    val suggestedIngredientsUiState by viewModel.suggestedIngredientsUiState.collectAsStateWithLifecycle()
    val errorDialogState = viewModel.errorDialogState.collectAsStateWithLifecycle()

    val onIngredientClick = remember { viewModel::onToggleIngredient }
    val dismissErrorDialog = remember { viewModel::dismissErrorDialog }

    OnHandAlertDialog(
        onDismiss = dismissErrorDialog,
        state = errorDialogState.value
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        IngredientSearchBar(
            onClick = onIngredientSearchClick,
            enabled = false
        )
        
        SuggestedIngredients(
            suggestedIngredientsState = suggestedIngredientsUiState,
            onIngredientClick = onIngredientClick
        )
        
        PantryItemList(
            pantryUiState,
            onIngredientClick
        )
    }
}

@Composable
private fun SuggestedIngredients(
    suggestedIngredientsState: SearchUiState,
    onIngredientClick: (UiPantryIngredient) -> Unit
) {
    Text(
        modifier = Modifier.padding(12.dp),
        text = "Suggested Ingredients",
        style = MaterialTheme.typography.titleLarge
    )
    
    when (suggestedIngredientsState) {
        SearchUiState.Loading -> {
            Box(modifier = Modifier.height(120.dp)) {
                OnHandProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        
        SearchUiState.Error -> {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                text = "Unable to load suggestions",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        SearchUiState.Empty -> {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                text = "No suggestions available",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        is SearchUiState.Content -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(suggestedIngredientsState.ingredients) { item ->
                    val uiPantryIngredient = UiPantryIngredient(
                        ingredient = item.ingredient,
                        inPantry = item.inPantry
                    )
                    SuggestedIngredientItem(
                        ingredient = item,
                        onClick = {
                            onIngredientClick(uiPantryIngredient)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuggestedIngredientItem(
    ingredient: UiSearchIngredient,
    onClick: () -> Unit
) {
    val isInPantry = ingredient.inPantry.value
    
    Card(
        modifier = Modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = if (isInPantry) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = ingredient.ingredient.name,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (isInPantry) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "In pantry",
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to pantry",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PantryItemList(
    pantryUiState: PantryUiState,
    onToggleFromPantry: (UiPantryIngredient) -> Unit
) {
    Log.d("[OnHand]", "PantryItemList recomposition")

    Text(
        modifier = Modifier.padding(12.dp),
        text = "Your Pantry",
        style = MaterialTheme.typography.displayMedium
    )

    when (pantryUiState) {
        PantryUiState.Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        is PantryUiState.Content -> {
            PantryCardList(
                pantry = pantryUiState.ingredients,
                onItemClick = onToggleFromPantry
            )
        }

        PantryUiState.Empty -> {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Your pantry is empty. You can add items by searching for " +
                        "ingredients.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        PantryUiState.Error -> {
            Log.d("[OnHand], ", "Error in PantryItemList")
        }
    }
}

private class PantryItemCard(
    val pantryIngredient: UiPantryIngredient
)

@Composable
private fun PantryListItem(
    card: PantryItemCard,
    onItemClicked: (UiPantryIngredient) -> Unit
) {
    Card(
        modifier = Modifier
            .clickable {
                onItemClicked(card.pantryIngredient)
            }
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = if (card.pantryIngredient.inPantry.value) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = card.pantryIngredient.ingredient.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (card.pantryIngredient.inPantry.value) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "In pantry",
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to pantry",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PantryCardList(
    pantry: List<UiPantryIngredient>,
    onItemClick: (UiPantryIngredient) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(pantry, key = { _, item -> item.ingredient.id }) { _, item ->
            PantryListItem(
                card = PantryItemCard(item),
                onItemClicked = onItemClick
            )
        }
    }
}
