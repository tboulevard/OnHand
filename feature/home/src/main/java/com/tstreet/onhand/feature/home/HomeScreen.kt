package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.common.R.string.add_to_pantry_content_description
import com.tstreet.onhand.core.common.R.string.error_message
import com.tstreet.onhand.core.common.R.string.in_pantry_content_description
import com.tstreet.onhand.core.common.R.string.no_suggestions_available
import com.tstreet.onhand.core.common.R.string.pantry_empty_message
import com.tstreet.onhand.core.common.R.string.pantry_empty_title
import com.tstreet.onhand.core.common.R.string.suggested_ingredients_title
import com.tstreet.onhand.core.common.R.string.unable_to_load_suggestions
import com.tstreet.onhand.core.common.R.string.your_pantry_title
import com.tstreet.onhand.core.model.data.Ingredient
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.ui.AlertDialogState
import com.tstreet.onhand.core.ui.AlertDialogState.Companion.dismissed
import com.tstreet.onhand.core.ui.IngredientSearchBar
import com.tstreet.onhand.core.ui.OnHandAlertDialog
import com.tstreet.onhand.core.ui.OnHandProgressIndicator

@Composable
fun HomeScreenContainer(
    viewModel: HomeViewModel,
    onIngredientSearchBarClicked: () -> Unit
) {
    Log.d("[OnHand]", "HomeScreen recomposition")
    val pantryUiState by viewModel.pantryUiState.collectAsStateWithLifecycle()
    val suggestedIngredientsUiState by viewModel.suggestedIngredientsUiState.collectAsStateWithLifecycle()
    val errorDialogState by viewModel.errorDialogState.collectAsStateWithLifecycle()

    val onToggleIngredient = remember { viewModel::onToggleIngredient }
    val onDismissErrorDialog = remember { viewModel::onDismissErrorDialog }

    HomeScreen(
        suggestedIngredientsUiState,
        pantryUiState,
        onIngredientSearchBarClicked,
        onToggleIngredient,
        errorDialogState,
        onDismissErrorDialog
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    suggestedIngredientsUiState: SearchUiState,
    pantryUiState: PantryUiState,
    onIngredientSearchClick: () -> Unit,
    onToggleIngredient: (UiPantryIngredient) -> Unit,
    errorDialogState: AlertDialogState,
    onDismissErrorDialog: () -> Unit
) {
    OnHandAlertDialog(
        onDismiss = onDismissErrorDialog,
        state = errorDialogState
    )

    Scaffold(
        topBar = {
            IngredientSearchBar(
                onClick = onIngredientSearchClick,
                enabled = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {

            Pantry(
                pantryUiState,
                onToggleIngredient
            )
        }
    }
}

@Composable
private fun SuggestedIngredients(
    suggestedIngredientsState: SearchUiState,
    onIngredientClick: (UiPantryIngredient) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
            text = stringResource(suggested_ingredients_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        when (suggestedIngredientsState) {
            SearchUiState.Loading -> {
                Box(modifier = Modifier.height(120.dp)) {
                    OnHandProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            SearchUiState.Error -> {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    text = stringResource(unable_to_load_suggestions),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            SearchUiState.Empty -> {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    text = stringResource(no_suggestions_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is SearchUiState.Content -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(suggestedIngredientsState.ingredients) { item ->
                        val uiPantryIngredient = UiPantryIngredient(
                            ingredient = item.ingredient,
                            inPantry = item.inPantry
                        )
                        SuggestedIngredientItem(
                            ingredient = item,
                            onClick = {
                                onIngredientClick(uiPantryIngredient)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestedIngredientItem(
    ingredient: UiSearchIngredient,
    onClick: () -> Unit
) {
    // Animation for color change
    val backgroundColor by animateColorAsState(
        targetValue = if (ingredient.inPantry.value)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "backgroundColorAnimation"
    )

    val contentColor by animateColorAsState(
        targetValue = if (ingredient.inPantry.value)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "contentColorAnimation"
    )

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(70.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = ingredient.ingredient.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (ingredient.inPantry.value) Icons.Filled.Check else Icons.Filled.Add,
                contentDescription = if (ingredient.inPantry.value)
                    stringResource(in_pantry_content_description)
                else
                    stringResource(add_to_pantry_content_description),
                modifier = Modifier.size(18.dp),
                tint = contentColor
            )
        }
    }
}

@Composable
private fun Pantry(
    pantryUiState: PantryUiState,
    onIngredientClick: (UiPantryIngredient) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            text = stringResource(your_pantry_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        when (pantryUiState) {
            PantryUiState.Loading -> {
                OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
            }

            is PantryUiState.Content -> {
                PantryIngredientList(
                    pantryIngredients = pantryUiState.ingredients,
                    onItemClick = onIngredientClick
                )
            }

            PantryUiState.Empty -> EmptyPantryMessage()
            PantryUiState.Error -> ErrorMessage()
        }
    }
}

@Composable
fun PantryIngredientList(
    pantryIngredients: List<UiPantryIngredient>,
    onItemClick: (UiPantryIngredient) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(pantryIngredients, key = { item -> item.ingredient.id }) { ingredient ->
            PantryListItem(
                ingredient,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun PantryListItem(
    ingredient: UiPantryIngredient,
    onItemClick: (UiPantryIngredient) -> Unit
) {
    // Animation for color change
    val isInPantry = ingredient.inPantry.value
    val backgroundColor by animateColorAsState(
        targetValue = if (isInPantry)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "backgroundColorAnimation"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isInPantry)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "contentColorAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onItemClick(ingredient)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = ingredient.ingredient.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (isInPantry) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(in_pantry_content_description),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 4.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(add_to_pantry_content_description),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyPantryMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(pantry_empty_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = stringResource(pantry_empty_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ErrorMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Text(
                    text = stringResource(error_message),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

// TODO: Look into only including previews in debug builds
@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    val samplePantryIngredients = listOf(
        UiPantryIngredient(
            ingredient = Ingredient(
                id = 1,
                name = "Chicken"
            ),
            inPantry = remember { mutableStateOf(true) }
        ),
        UiPantryIngredient(
            ingredient = Ingredient(
                id = 2,
                name = "Bell Pepper"
            ),
            inPantry = remember { mutableStateOf(true) }
        ),
        UiPantryIngredient(
            ingredient = Ingredient(
                id = 3,
                name = "Olive Oil"
            ),
            inPantry = remember { mutableStateOf(true) }
        ),
        UiPantryIngredient(
            ingredient = Ingredient(
                id = 4,
                name = "Tomatoes"
            ),
            inPantry = remember { mutableStateOf(false) }
        )
    )

    val sampleSuggestedIngredients = listOf(
        UiSearchIngredient(
            ingredient = Ingredient(
                id = 5,
                name = "Garlic"
            ),
            inPantry = remember { mutableStateOf(false) }
        ),
        UiSearchIngredient(
            ingredient = Ingredient(
                id = 6,
                name = "Black Pepper"
            ),
            inPantry = remember { mutableStateOf(true) }
        ),
        UiSearchIngredient(
            ingredient = Ingredient(
                id = 7,
                name = "Salt"
            ),
            inPantry = remember { mutableStateOf(false) }
        )
    )

    MaterialTheme {
        HomeScreen(
            suggestedIngredientsUiState = SearchUiState.Content(sampleSuggestedIngredients),
            pantryUiState = PantryUiState.Content(samplePantryIngredients),
            onIngredientSearchClick = {},
            onToggleIngredient = {},
            errorDialogState = dismissed(),
            onDismissErrorDialog = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeScreenEmptyPantryPreview() {
    val sampleSuggestedIngredients = listOf(
        UiSearchIngredient(
            ingredient = Ingredient(
                id = 5,
                name = "Garlic"
            ),
            inPantry = remember { mutableStateOf(false) }
        ),
        UiSearchIngredient(
            ingredient = Ingredient(
                id = 6,
                name = "Black Pepper"
            ),
            inPantry = remember { mutableStateOf(true) }
        )
    )

    HomeScreen(
        suggestedIngredientsUiState = SearchUiState.Content(sampleSuggestedIngredients),
        pantryUiState = PantryUiState.Empty,
        onIngredientSearchClick = {},
        onToggleIngredient = {},
        errorDialogState = dismissed(),
        onDismissErrorDialog = {}
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeScreenErrorStatePreview() {
    HomeScreen(
        suggestedIngredientsUiState = SearchUiState.Error,
        pantryUiState = PantryUiState.Error,
        onIngredientSearchClick = {},
        onToggleIngredient = {},
        errorDialogState = dismissed(),
        onDismissErrorDialog = {}
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeScreenLoadingStatePreview() {
    HomeScreen(
        suggestedIngredientsUiState = SearchUiState.Loading,
        pantryUiState = PantryUiState.Loading,
        onIngredientSearchClick = {},
        onToggleIngredient = {},
        errorDialogState = dismissed(),
        onDismissErrorDialog = {}
    )
}

@Preview(showBackground = true, widthDp = 180, heightDp = 80)
@Composable
fun SuggestedIngredientItemPreview() {
    SuggestedIngredientItem(
        ingredient = UiSearchIngredient(
            ingredient = Ingredient(
                id = 1,
                name = "Bell Pepper"
            ),
            inPantry = remember { mutableStateOf(false) }
        ),
        onClick = {}
    )
}

@Preview(showBackground = true, widthDp = 180, heightDp = 80)
@Composable
fun SuggestedIngredientItemInPantryPreview() {
    SuggestedIngredientItem(
        ingredient = UiSearchIngredient(
            ingredient = Ingredient(
                id = 1,
                name = "Bell Pepper"
            ),
            inPantry = remember { mutableStateOf(true) }
        ),
        onClick = {}
    )
}

@Preview(showBackground = true, widthDp = 180)
@Composable
fun PantryListItemPreview() {
    PantryListItem(
        ingredient = UiPantryIngredient(
            ingredient = Ingredient(
                id = 1,
                name = "Chicken"
            ),
            inPantry = remember { mutableStateOf(true) }
        ),
        onItemClick = {}
    )
}

@Preview(showBackground = true, widthDp = 180)
@Composable
fun PantryListItemNotInPantryPreview() {
    PantryListItem(
        ingredient = UiPantryIngredient(
            ingredient = Ingredient(
                id = 2,
                name = "Tomatoes"
            ),
            inPantry = remember { mutableStateOf(false) }
        ),
        onItemClick = {}
    )
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun EmptyPantryMessagePreview() {
    EmptyPantryMessage()
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun ErrorMessagePreview() {
    ErrorMessage()
}