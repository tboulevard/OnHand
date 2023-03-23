package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tstreet.onhand.core.model.Ingredient
import com.tstreet.onhand.core.ui.FullScreenProgressIndicator
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN

// TODO: use @PreviewParameter + create module with fake models to populate composables
@Composable
fun IngredientSearchScreen(
    navController: NavController,
    viewModel: IngredientSearchViewModel
) {
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        IngredientSearchBar(searchText, viewModel::onSearchTextChange)

        when {
            isSearching -> {
                FullScreenProgressIndicator()
            }
            else -> {
                IngredientSearchCardList(
                    ingredients = viewModel.ingredients,
                    onItemClick = viewModel::onToggleFromSearch
                )
            }
        }

        // Pantry Header
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            text = "Your Pantry",
            style = MaterialTheme.typography.displayMedium
        )

        when {
            viewModel.pantry.isEmpty() -> {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    text = "(empty)",
                    style = MaterialTheme.typography.displaySmall
                )
            }
            else -> {
                PantryCardList(
                    pantry = viewModel.pantry,
                    onItemClick = viewModel::onToggleFromPantry
                )
            }
        }

        // Find Recipes Button
        Button(
            onClick = {
                navController.navigate("recipe_search")
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Find Recipes",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun IngredientSearchBar(
    searchText: String,
    onTextChanged: (String) -> Unit,
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onTextChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "search",
                modifier = Modifier.size(24.dp)
            )
        },
        label = {
            Text(
                text = "Search Ingredients",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium
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
            .height(72.dp)
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .clickable(onClick = { onItemClicked(index) })
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = card.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 8.dp)
            )
            if (card.inPantry) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Added to pantry",
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun IngredientSearchCardList(
    ingredients: List<Ingredient>,
    onItemClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(
            items = ingredients
        ) { index, ingredient ->
            IngredientSearchListItem(
                card = IngredientSearchCard(
                    name = ingredient.name,
                    inPantry = ingredient.inPantry
                ),
                index,
                onItemClicked = onItemClick
            )
        }
    }
}

private class PantryItemCard(
    val ingredientName: String,
    val inPantry: Boolean
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
    pantry: List<Ingredient>,
    onItemClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 96.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        itemsIndexed(pantry) { index, ingredient ->
            PantryListItem(
                card = PantryItemCard(
                    ingredient.name,
                    ingredient.inPantry
                ),
                index = index,
                onItemClicked = onItemClick
            )
        }
    }
}
