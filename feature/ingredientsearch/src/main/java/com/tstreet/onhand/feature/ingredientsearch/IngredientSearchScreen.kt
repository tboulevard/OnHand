package com.tstreet.onhand.feature.ingredientsearch

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tstreet.onhand.core.model.Ingredient

@Composable
fun IngredientSearchScreen(
    navController: NavController,
    viewModel: IngredientSearchViewModel
) {
    val searchText by viewModel.searchText.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
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
                placeholder = { Text(text = "Search Ingredients") },
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
                items(items = ingredients) { ingredient ->

//                    ListItem(item = it) {
//
//                    }
                    Button(
                        onClick = {
                            if (ingredient.inPantry) {
                                viewModel.removeIngredientFromPantry(ingredient)
                            } else {
                                viewModel.addIngredientToPantry(ingredient)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = ingredient.name + " / inPantry: " + ingredient.inPantry)
                    }
                }
            }
        }
    }
}

//@Composable
//// TODO: implement by marking items as selected first, then iterate to make it include items added to pantry
//private fun ListItem(item: Ingredient, onItemClick: (Int) -> Unit) {
//    Column(
//        modifier = Modifier.border(3.dp, Color.Green)
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable {
//                    onItemClick(item.id)
//                }
//                .padding(8.dp)
//        ) {
//            Text("Index: Name ${item.name}", fontSize = 20.sp)
//            if (item.isSelected) {
//                Icon(
//                    modifier = Modifier
//                        .align(Alignment.CenterEnd)
//                        .background(Color.Red, CircleShape),
//                    imageVector = Icons.Default.Check,
//                    contentDescription = "Selected",
//                    tint = Color.Green,
//                )
//            }
//        }
//    }
//}
