package com.tstreet.onhand.feature.ingredientsearch

import android.util.Log
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN

// TODO: should be reusable with the only difference being the onclick behavior of the ingredient search
// list item
@Composable
fun IngredientSearchScreen(
    viewModel: IngredientSearchViewModel
) {
    val uiState by viewModel.searchUiState.collectAsState()
    val searchText by viewModel.displayedSearchText.collectAsState()

    val onIngredientSearchTextChanged = remember { viewModel::onSearchTextChanged }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        IngredientSearchBar(
            searchText = searchText,
            onTextChanged = onIngredientSearchTextChanged
        )
        IngredientSearchCardList(
            searchUiState = uiState,
            onItemClick = { ingredient ->
                viewModel.onItemClick(ingredient)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientSearchBar(
    searchText: String,
    onTextChanged: (String) -> Unit
) {
    val isFocused = remember { mutableStateOf<Boolean>(false) }
    val focusRequester = remember { FocusRequester() }

    // Difference b/w this and launched effect?
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)
            .onFocusChanged {
                isFocused.value = it.isFocused
            }.focusRequester(focusRequester),
        value = searchText,
        singleLine = true,
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
            if (!isFocused.value) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "search",
                )
            } else {
                IconButton(onClick = {
                    // TODO: GO BACK ACTION
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
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledIndicatorColor = Color.Transparent,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
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
            .clickable(onClick = { onItemClicked(ingredient) }),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (card.inPantry) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "In pantry",
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Text(
                text = card.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            if (card.isSelected) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Not selected",
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun IngredientSearchCardList(
    searchUiState: SearchUiState,
    onItemClick: (UiSearchIngredient) -> Unit
) {
    Log.d("[OnHand]", "IngredientSearchCardList recomposition")

    when (searchUiState) {
        is SearchUiState.Empty -> {
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

        is SearchUiState.Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        is SearchUiState.Content -> {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    key = { position, item -> item.ingredient.id },
                    items = searchUiState.ingredients
                ) { index, item ->
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
            Log.d("[OnHand]", "Error in IngredientSearchCardList")
        }
    }
}

