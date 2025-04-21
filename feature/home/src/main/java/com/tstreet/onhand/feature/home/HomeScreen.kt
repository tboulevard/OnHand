package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.model.ui.HomeViewUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.ui.OnHandAlertDialog
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN

// TODO: use @PreviewParameter + create module with fake models to populate composables
// TODO: screen rotation wipes `isSearchBarFocused` -> look into used collectAsStateWithLifecycle
// TODO: Using the hardware back/swipe back while in search doesn't nav back to pantry. Eventually
//  we'll probably want IngredientSearch as a separate screen so we can add it to the nav backstack
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val pantry by viewModel.pantry.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchText by viewModel.displayedSearchText.collectAsStateWithLifecycle()
    val isSearchBarFocused by viewModel.isSearchBarFocused.collectAsStateWithLifecycle()
    val errorDialogState = viewModel.errorDialogState.collectAsStateWithLifecycle()

    val onIngredientSearchTextChanged = remember { viewModel::onSearchTextChanged}
    val onIngredientSearchListClick = remember { viewModel::onToggleFromSearch }
    val onSearchBarFocusChanged = remember { viewModel::onSearchBarFocusChanged }
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
            searchText = searchText,
            onTextChanged = onIngredientSearchTextChanged,
            onFocusChanged = onSearchBarFocusChanged,
            isFocused = isSearchBarFocused
        )
        when {
            isSearchBarFocused -> {
                IngredientSearchCardList(
                    uiState = uiState,
                    onItemClick = onIngredientSearchListClick
                )
            }

            else -> {
                PantryItemList(
                    pantry,
                    viewModel::onToggleFromPantry
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientSearchBar(
    searchText: String,
    onTextChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isFocused: Boolean,
) {
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)
            .onFocusChanged { onFocusChanged(it.isFocused) },
        value = searchText,
        onValueChange = {
            onTextChanged(it)
        },
        placeholder = { Text("Search Ingredients") },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                Icon(
                    modifier = Modifier.clickable {
                        onTextChanged("")
                    },
                    imageVector = Icons.Filled.Close,
                    contentDescription = "close"
                )
            }
        },
        leadingIcon = {
            if (!isFocused) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "search",
                )
            } else {
                IconButton(onClick = {
                    onTextChanged("")
                    onFocusChanged(false)
                    focusManager.clearFocus()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "search",
                    )
                }
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

private class IngredientSearchCard(
    val name: String,
    val inPantry: Boolean
)

@Composable
private fun IngredientSearchListItem(
    card: IngredientSearchCard,
    ingredient: UiPantryIngredient,
    onItemClicked: (UiPantryIngredient) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (card.inPantry) {
                MATTE_GREEN
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClicked(ingredient) }),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .recomposeHighlighter(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = card.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            if (card.inPantry) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Added to pantry",
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Not in pantry",
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun IngredientSearchCardList(
    uiState: HomeViewUiState,
    onItemClick: (UiPantryIngredient) -> Unit
) {
    when (uiState) {
        is HomeViewUiState.Empty -> {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No results.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        is HomeViewUiState.Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        is HomeViewUiState.Content -> {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    key = { position, item -> item.ingredient.id },
                    items = uiState.ingredients
                ) { index, item ->
                    IngredientSearchListItem(
                        card = IngredientSearchCard(
                            name = item.ingredient.name,
                            inPantry = item.inPantry.value
                        ),
                        item,
                        onItemClicked = onItemClick
                    )
                }
            }
        }

        is HomeViewUiState.Error -> {
            Log.d("[OnHand], ", "Error in IngredientSearchCardList")
        }
    }
}

@Composable
private fun PantryItemList(
    pantry: List<Ingredient>,
    onToggleFromPantry: (Int) -> Unit
) {
    Text(
        modifier = Modifier.padding(12.dp),
        text = "Your Pantry",
        style = MaterialTheme.typography.displayMedium
    )

    when {
        pantry.isEmpty() -> {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Your pantry is empty. You can add items by searching for " +
                        "ingredients.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        else -> {
            PantryCardList(
                pantry = pantry,
                onItemClick = onToggleFromPantry
            )
        }
    }
}

private class PantryItemCard(
    val ingredientName: String
)

@Composable
private fun PantryListItem(
    card: PantryItemCard,
    index: Int,
    onItemClicked: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .clickable {
                onItemClicked(index)
            }
            .padding(2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiary),
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
                text = card.ingredientName,
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
    pantry: List<Ingredient>,
    onItemClick: (Int) -> Unit
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
        itemsIndexed(pantry, key = { _, item -> item.id }) { index, item ->
            PantryListItem(
                card = PantryItemCard(
                    item.name,
                ),
                index = index,
                onItemClicked = onItemClick
            )
        }
    }
}
