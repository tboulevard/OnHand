package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(items = viewModel.ingredients) { index, ingredient ->
                    IngredientSearchListItem(
                        ingredient = ingredient,
                        index = index,
                        viewModel = viewModel
                    )
                }
            }
        }
        Row {
            Text(
                "Pantry"
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .border(3.dp, randomColor())
            ) {
                // TODO: Make the grid elements have adaptive size based on the number of characters
                // TODO: in the ingredient.
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 72.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(viewModel.pantry) { index, ingredient ->
                        PantryListItem(
                            ingredient = ingredient,
                            index = index,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

// TODO: cleanup below...
@Composable
// TODO: have better encapsulation by not passing view model as arg
private fun IngredientSearchListItem(
    ingredient: Ingredient,
    index: Int,
    viewModel: IngredientSearchViewModel
) {
    Column(
        modifier = Modifier.border(3.dp, randomColor())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.onTogglePantryState(index)
                }
                .padding(8.dp)
        ) {
            Text(ingredient.name, fontSize = 20.sp)
            if (ingredient.inPantry) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    imageVector = Icons.Default.Check,
                    contentDescription = "Added to pantry",
                    tint = Color.Green,
                )
            }
        }
    }
}

@Composable
// TODO: have better encapsulation by not passing view model as arg
// TODO: make it so panty items can be removed from here too
private fun PantryListItem(
    ingredient: Ingredient,
    index: Int,
    viewModel: IngredientSearchViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                viewModel.onTogglePantryStateFromPantry(index)
            }
            .border(3.dp, randomColor()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            fontSize = 12.sp,
            text = ingredient.name,
        )
    }
}

fun randomColor() = Color(
    Random.nextInt(256),
    Random.nextInt(256),
    Random.nextInt(256),
    alpha = 255
)
