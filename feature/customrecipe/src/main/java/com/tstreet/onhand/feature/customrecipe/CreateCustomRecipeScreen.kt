package com.tstreet.onhand.feature.customrecipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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
    val scrollState = rememberScrollState()
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Recipe") },
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            BottomAppBar {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        viewModel.onSaveRecipe(ingredientSearchViewModel.selectedIngredients)
                    },
                    enabled = ingredients.isNotEmpty() && isTitleValid.value,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "Save Recipe")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recipe Title Section
            OutlinedTextField(
                value = title.value,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Recipe Title") },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                isError = inputValidationText.value.shown,
                supportingText = if (inputValidationText.value.shown) {
                    {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Title input error",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = inputValidationText.value.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else null
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { viewModel.onImageChanged("") },
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add cover image",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add Cover Image",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Ingredients Section
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(PANTRY_INGREDIENT_SEARCH_ROUTE)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add ingredients",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Add ingredients",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    if (ingredients.isNotEmpty()) {
                        Divider()
                        Column {
                            ingredients.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = {
                                        // TODO: handle remove ingredient
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove ingredient",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                if (index < ingredients.lastIndex) {
                                    Divider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(40.dp))  
                            Text(
                                "No ingredients added yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Instructions Section
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            OutlinedTextField(
                value = instructions.value ?: "",
                onValueChange = viewModel::onInstructionsChanged,
                label = { Text("Cooking steps (optional)") },
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                maxLines = 8
            )
            
            Spacer(modifier = Modifier.height(80.dp)) 
        }
    }
}