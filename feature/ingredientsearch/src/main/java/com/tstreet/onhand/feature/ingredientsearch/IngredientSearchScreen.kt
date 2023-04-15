package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tstreet.onhand.core.model.PantryIngredient
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.theming.MATTE_GREEN

// TODO: use @PreviewParameter + create module with fake models to populate composables
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSearchScreen(
    navController: NavController,
    viewModel: IngredientSearchViewModel
) {
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    println("[OnHand] expanded=$expanded")
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
                println("[OnHand] onExpandedChanged($expanded)")
            },
            modifier = Modifier
                .padding(8.dp)
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = searchText,
                onValueChange = {
                    viewModel.onSearchTextChange(it)
                    expanded = it.isNotEmpty()
                },
                placeholder = { Text("Search Ingredients") },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickable {
                                viewModel.onSearchTextChange("")
                                expanded = true
                            },
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close"
                        )
                    }
                },
                leadingIcon = {
                    if (!expanded) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "search"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "arrow_back"
                        )
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.textFieldColors(
                    //disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    //disabledIndicatorColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                modifier = Modifier
                    .exposedDropdownSize(),
                expanded = expanded,
                onDismissRequest = {
                    println("[OnHand] onDismissRequest($expanded)")
                }
            ) {
                if (isSearching) {
                    OnHandProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(CenterHorizontally)
                    )
                } else {
                    viewModel.ingredients.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            modifier = Modifier
                                .background(
                                    if (item.inPantry) {
                                        MATTE_GREEN
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentPadding = PaddingValues(4.dp),
                            onClick = {
                                viewModel.onToggleFromSearch(index)
                            },
                            text = {
                                Text(
                                    text = item.ingredient.name
                                )
                            },
                            trailingIcon = {
                                if (item.inPantry) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Added to pantry",
                                        tint = MaterialTheme.colorScheme.inverseOnSurface,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }

        // Pantry Header
        Text(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
            text = "Pantry",
            style = MaterialTheme.typography.displayMedium
        )

        when {
            viewModel.pantry.isEmpty() -> {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    text = "Your pantry is empty.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {
                PantryCardList(
                    pantry = viewModel.pantry,
                    onItemClick = viewModel::onToggleFromPantry
                )
            }
        }
    }
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

    println("[OnHand] Creating dropdown menu item: ${card.name}")
    DropdownMenuItem(
        text = {
            Text(
                text = card.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        },
        onClick = { onItemClicked(index) },
        trailingIcon = {
            if (card.inPantry) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Added to pantry",
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        },
    )


//    Card(
//        shape = MaterialTheme.shapes.medium,
//        colors = CardDefaults.cardColors(
//            containerColor = if (card.inPantry) {
//                MATTE_GREEN
//            } else {
//                MaterialTheme.colorScheme.surfaceVariant
//            },
//        ),
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(72.dp)
//            .padding(vertical = 2.dp, horizontal = 4.dp)
//            .clickable(onClick = { onItemClicked(index) })
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(end = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = card.name,
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .padding(horizontal = 8.dp)
//            )
//            if (card.inPantry) {
//                Icon(
//                    imageVector = Icons.Default.Check,
//                    contentDescription = "Added to pantry",
//                    tint = MaterialTheme.colorScheme.inverseOnSurface,
//                    modifier = Modifier.align(Alignment.CenterVertically)
//                )
//            }
//        }
//    }
}

@Composable
fun IngredientSearchCardList(
    ingredients: List<PantryIngredient>,
    onItemClick: (Int) -> Unit
) {
    ingredients.forEachIndexed { index, item ->
        println("[OnHand] ingredients.forEachIndexed (${item.ingredient.name})")
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
                    item.inPantry
                ),
                index = index,
                onItemClicked = onItemClick
            )
        }
    }
}
