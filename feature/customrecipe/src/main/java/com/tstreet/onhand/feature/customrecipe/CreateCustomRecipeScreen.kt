package com.tstreet.onhand.feature.customrecipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.common.CREATE_RECIPE_ROUTE
import com.tstreet.onhand.core.common.PANTRY_INGREDIENT_SEARCH_ROUTE
import com.tstreet.onhand.core.common.RECIPE_DETAIL_ROUTE
import com.tstreet.onhand.core.model.ui.SelectableIngredient
import com.tstreet.onhand.core.ui.OnHandAlertDialog
import com.tstreet.onhand.core.ui.OnHandScreenHeader
import com.tstreet.onhand.feature.ingredientsearch.SelectableIngredientSearchViewModel

// TODO: use @PreviewParameter + create module with fake models to populate composables
// TODO: screen rotation wipes `isSearchBarFocused` -> look into used collectAsStateWithLifecycle
// TODO: Using the hardware back/swipe back while in search doesn't nav back to pantry. Eventually
//  we'll probably want IngredientSearch as a separate screen so we can add it to the nav backstack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomRecipeScreen(
    navController: NavHostController,
    viewModel: CreateCustomRecipeViewModel,
    ingredientSearchViewModel: SelectableIngredientSearchViewModel
) {

    // TODO: nav away warn unsaved changes

    val title = viewModel.title.collectAsState()
    val ingredients = ingredientSearchViewModel.selectedIngredients
    val isTitleValid = viewModel.isTitleValid.collectAsStateWithLifecycle()
    val inputValidationText = viewModel.titleInputValidationState.collectAsStateWithLifecycle()
    val instructions = viewModel.instructions.collectAsState()
    val errorDialogState by viewModel.errorDialogState.collectAsStateWithLifecycle()
    val recipeId = viewModel.createdRecipeId.collectAsStateWithLifecycle()

    LaunchedEffect(recipeId.value) {
        recipeId.value?.let {
            navController.navigate("$RECIPE_DETAIL_ROUTE/${recipeId.value}") {
                // To pop this and ingredient search viewmodels off backstack. Allows onCleared to
                // be called for both in navigation subgraph.
                popUpTo(CREATE_RECIPE_ROUTE) {
                    inclusive = true
                }
            }
        }
    }

    // For general errors
    OnHandAlertDialog(
        onDismiss = viewModel::dismissErrorDialog, state = errorDialogState
    )

    Column() {
        OnHandScreenHeader(text = "Create Recipe")
        TextField(
            value = title.value,
            onValueChange = viewModel::onTitleChanged,
            label = { Text("Recipe Title") },
            textStyle = MaterialTheme.typography.bodyMedium,
        )
        if (inputValidationText.value.shown) {
            Row {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "title input error",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = inputValidationText.value.message
                )
            }
        }

        IconButton(modifier = Modifier.size(144.dp), onClick = { viewModel.onImageChanged("") }) {
            Column() {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "add image",
                    modifier = Modifier.fillMaxWidth(),
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
                Text(text = "Add Cover Image")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    navController.navigate(PANTRY_INGREDIENT_SEARCH_ROUTE)
                }, horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "add ingredients",
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.surfaceTint
            )
            Text("Add ingredients")
        }

        // TODO: if this list exceeds bounds of screen, only this section is scrollable.
        //  make entire screen scrollable (similar approach to shopping list)
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(ingredientSearchViewModel.selectedIngredients) { index, item ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.name)
                    Icon(Icons.Default.Delete,
                        contentDescription = "remove ingredient",
                        modifier = Modifier
                            .clickable {
                                // TODO: handle remove ingredient
                            }
                            .size(32.dp)
                            .padding(4.dp)
                            .align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.surfaceTint)
                }
            }
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = instructions.value ?: "",
            onValueChange = viewModel::onInstructionsChanged,
            label = { Text("Instructions (optional)") },
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = {
                viewModel.onSaveRecipe(ingredientSearchViewModel.selectedIngredients)
            }, enabled = ingredientSearchViewModel.selectedIngredients.isNotEmpty() && isTitleValid.value
        ) {
            Text(text = "Done")
        }
    }
}
