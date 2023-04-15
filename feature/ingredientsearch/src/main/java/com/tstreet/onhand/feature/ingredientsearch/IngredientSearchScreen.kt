package com.tstreet.onhand.feature.ingredientsearch

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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.PantryIngredient
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN

// TODO: use @PreviewParameter + create module with fake models to populate composables
@Composable
fun IngredientSearchScreen(
    viewModel: IngredientSearchViewModel
) {
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val isSearchBarFocused by viewModel.isSearchBarFocused.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        IngredientSearchBar(
            searchText = searchText,
            onTextChanged = viewModel::onSearchTextChanged,
            onFocusChanged = viewModel::onSearchBarFocusChanged,
            isFocused = isSearchBarFocused
        )
        when {
            isSearching -> {
                OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            isSearchBarFocused -> {
                IngredientSearchCardList(
                    ingredients = viewModel.ingredients,
                    onItemClick = viewModel::onToggleFromSearch
                )
            }
            else -> {
                PantryItemList(
                    viewModel.pantry,
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
            .padding(8.dp)
            .onFocusChanged { onFocusChanged(it.isFocused) },
        value = searchText,
        onValueChange = { onTextChanged(it) },
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
    index: Int,
    onItemClicked: (Int) -> Unit
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
            .clickable(onClick = { onItemClicked(index) }),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
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
                    imageVector = Icons.Default.Check,
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
    ingredients: List<PantryIngredient>,
    onItemClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(
            items = ingredients
        ) { index, item ->
            IngredientSearchListItem(
                card = IngredientSearchCard(
                    name = item.ingredient.name,
                    inPantry = item.inPantry
                ),
                index,
                onItemClicked = onItemClick
            )
        }
    }
}

@Composable
private fun PantryItemList(
    pantry: SnapshotStateList<PantryIngredient>,
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
            .fillMaxWidth()
            .clickable {
                onItemClicked(index)
            }
            .height(56.dp)
            .padding(2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = card.ingredientName,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PantryCardList(
    pantry: List<PantryIngredient>,
    onItemClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 96.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        itemsIndexed(pantry) { index, item ->
            PantryListItem(
                card = PantryItemCard(
                    item.ingredient.name,
                ),
                index = index,
                onItemClicked = onItemClick
            )
        }
    }
}