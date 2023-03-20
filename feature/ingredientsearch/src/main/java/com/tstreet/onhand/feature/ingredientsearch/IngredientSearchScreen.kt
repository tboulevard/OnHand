package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tstreet.onhand.core.model.Ingredient
import kotlin.random.Random

@Composable
// TODO before merge: ingredient search shows first 7 items from db without search text entered
fun IngredientSearchScreen(
    navController: NavController,
    viewModel: IngredientSearchViewModel
) {
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                        tint = Color.Black,
                    ) },
                label = { Text(text = "Search Ingredients") },
            )
        }
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                itemsIndexed(
                    items = viewModel.ingredients
                ) { index, ingredient ->
                    IngredientSearchListItem(
                        ingredient = ingredient,
                        index,
                        onItemClicked = viewModel::onToggleFromSearch
                    )
                }
            }
        }
        Row {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                text = "Your Pantry",
                fontSize = 24.sp
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (viewModel.pantry.isEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    text = "(empty)",
                    fontSize = 16.sp
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(3.dp, Color.DarkGray)
                ) {
                    // TODO: Make the grid elements have adaptive size based on the number of characters
                    // TODO: in the ingredient.
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 72.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        itemsIndexed(viewModel.pantry) { index, ingredient ->
                            PantryListItem(
                                ingredient = ingredient,
                                index = index,
                                onItemClicked = viewModel::onToggleFromPantry
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                navController.navigate("recipe_search")
            }
        ) {
            Text(text = "Find Recipes")
        }
    }
}

@Composable
private fun IngredientSearchListItem(
    ingredient: Ingredient,
    index: Int,
    onItemClicked: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .border(
                3.dp,
                if (ingredient.inPantry) {
                    MATTE_GREEN
                } else {
                    randomColor()
                }
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onItemClicked(index)
                }
        ) {
            Text(ingredient.name, fontSize = 20.sp)
            if (ingredient.inPantry) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    imageVector = Icons.Default.Check,
                    contentDescription = "Added to pantry",
                    tint = MATTE_GREEN,
                )
            }
        }
    }
}

@Composable
private fun PantryListItem(
    ingredient: Ingredient,
    index: Int,
    onItemClicked: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                onItemClicked(index)
            }
            .border(2.dp, randomColor())
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            fontSize = 12.sp,
            text = ingredient.name,
        )
    }
}

val MATTE_GREEN = Color(72, 161, 77)

// Keeping around for testing recomposition later
fun randomColor() = Color(
    Random.nextInt(256),
    Random.nextInt(256),
    Random.nextInt(256),
    alpha = 255
)
