package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.model.PantryIngredient
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN

// TODO: use @PreviewParameter + create module with fake models to populate composables
// TODO: screen rotation wipes `isSearchBarFocused` -> look into used collectAsStateWithLifecycle
// TODO: Using the hardware back/swipe back while in search doesn't nav back to pantry. Eventually
//  we'll probably want IngredientSearch as a separate screen so we can add it to the nav backstack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSearchScreen(
    navController: NavHostController,
    viewModel: IngredientSearchViewModel
) {
    val ingredients by viewModel.displayedIngredients.collectAsState()
    val searchText by viewModel.displayedSearchText.collectAsState(initial = "")
    val isSearching by viewModel.isSearching.collectAsState()
    val isSearchBarFocused by viewModel.isSearchBarFocused.collectAsState()
    val isPreSearchDebouncing by viewModel.isPreSearchDebounce.collectAsState()

    println("[OnHand] recompose IngredientSearchScreen")

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
                    ingredients = ingredients,
                    onItemClick = viewModel::onToggleIngredient,
                    isPreSearchDebouncing,
                    searchText
                )
            }
            else -> {
                Text("TODO - not implemented ")
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
    val isSelected: Boolean
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
            containerColor = if (card.isSelected) {
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
            if (card.isSelected) {
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
    ingredients: List<SelectableIngredient>,
    onItemClick: (Int) -> Unit,
    isPreDebounce: Boolean,
    searchText: String,

    ) {
    when (ingredients.isEmpty() && !isPreDebounce && searchText.isNotEmpty()) {
        true -> {
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
        else -> {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    items = ingredients
                ) { index, item ->
                    println("[OnHand] recomposition for ${item.ingredient.name}")
                    IngredientSearchListItem(
                        card = IngredientSearchCard(
                            name = item.ingredient.name,
                            isSelected = item.isSelected
                        ),
                        index,
                        onItemClicked = onItemClick
                    )
                }
            }
        }
    }
}
