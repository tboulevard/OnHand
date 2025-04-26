package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.common.recomposeHighlighter
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.ui.OnHandAlertDialog
import com.tstreet.onhand.core.ui.OnHandProgressIndicator

// TODO: use @PreviewParameter + create module with fake models to populate composables
// TODO: screen rotation wipes `isSearchBarFocused` -> look into used collectAsStateWithLifecycle
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onIngredientSearchClick: () -> Unit
) {
    Log.d("[OnHand]", "HomeScreen recomposition")
    val pantryUiState by viewModel.pantryUiState.collectAsStateWithLifecycle()
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
            onIngredientSearch = onIngredientSearchClick
        )
        PantryItemList(
            pantryUiState,
            onIngredientClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientSearchBar(
    onIngredientSearch: () -> Unit
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)
            .clickable {
                // Navigate to the ingredient search screen when clicking on the search bar
                onIngredientSearch.invoke()
            },
        value = "",
        onValueChange = {},
        enabled = false, // Disable the TextField since we're now using it as a button
        placeholder = { Text("Search Ingredients") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "search",
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledIndicatorColor = Color.Transparent,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
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
            .padding(2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = if (card.pantryIngredient.inPantry.value) {
            CardDefaults.cardColors(MaterialTheme.colorScheme.tertiary)
        } else {
            CardDefaults.cardColors(Color.Transparent)
        },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .align(Alignment.End)
        ) {
            Text(
                text = card.pantryIngredient.ingredient.name,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear from pantry",
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun PantryCardList(
    pantry: List<UiPantryIngredient>,
    onItemClick: (UiPantryIngredient) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(96.dp),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Top),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Start),
    ) {
        itemsIndexed(pantry, key = { _, item -> item.ingredient.id }) { index, item ->
            PantryListItem(
                card = PantryItemCard(item),
                onItemClicked = onItemClick
            )
        }
    }
}
