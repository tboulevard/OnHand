package com.tstreet.onhand.feature.home

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tstreet.onhand.core.model.ui.PantryUiState
import com.tstreet.onhand.core.model.ui.SearchUiState
import com.tstreet.onhand.core.model.ui.UiPantryIngredient
import com.tstreet.onhand.core.model.ui.UiSearchIngredient
import com.tstreet.onhand.core.ui.IngredientSearchBar
import com.tstreet.onhand.core.ui.OnHandAlertDialog
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onIngredientSearchClick: () -> Unit
) {
    Log.d("[OnHand]", "HomeScreen recomposition")
    val pantryUiState by viewModel.pantryUiState.collectAsStateWithLifecycle()
    val suggestedIngredientsUiState by viewModel.suggestedIngredientsUiState.collectAsStateWithLifecycle()
    val errorDialogState = viewModel.errorDialogState.collectAsStateWithLifecycle()

    val onIngredientClick = remember { viewModel::onToggleIngredient }
    val dismissErrorDialog = remember { viewModel::dismissErrorDialog }

    OnHandAlertDialog(
        onDismiss = dismissErrorDialog,
        state = errorDialogState.value
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        IngredientSearchBar(
            onClick = onIngredientSearchClick,
            enabled = false
        )
        
        SuggestedIngredients(
            suggestedIngredientsState = suggestedIngredientsUiState,
            onIngredientClick = onIngredientClick
        )
        
        PantryItemList(
            pantryUiState,
            onIngredientClick
        )
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
            text = "Suggested Ingredients",
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
                    text = "Unable to load suggestions",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            SearchUiState.Empty -> {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    text = "No suggestions available",
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
    val isInPantry = ingredient.inPantry.value
    
    // Animation for color change
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
    
    // Scale animation on click
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )
    
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(70.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100)
                isPressed = false
            }
        }
        
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
                imageVector = if (isInPantry) Icons.Filled.Check else Icons.Filled.Add,
                contentDescription = if (isInPantry) "In pantry" else "Add to pantry",
                modifier = Modifier.size(18.dp),
                tint = contentColor
            )
        }
    }
}

@Composable
private fun PantryItemList(
    pantryUiState: PantryUiState,
    onToggleFromPantry: (UiPantryIngredient) -> Unit
) {
    Log.d("[OnHand]", "PantryItemList recomposition")

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            text = "Your Pantry",
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
                PantryCardList(
                    pantry = pantryUiState.ingredients,
                    onItemClick = onToggleFromPantry
                )
            }

            PantryUiState.Empty -> {
                EmptyPantryMessage()
            }

            PantryUiState.Error -> {
                Log.d("[OnHand], ", "Error in PantryItemList")
                ErrorMessage()
            }
        }
    }
}

@Composable
private fun EmptyPantryMessage() {
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
                    text = "Your pantry is empty",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "You can add items by searching for ingredients",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage() {
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
                modifier = Modifier.padding(24.dp),
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
                    text = "Something went wrong",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

private class PantryItemCard(
    val pantryIngredient: UiPantryIngredient
)

@Composable
private fun PantryListItem(
    card: PantryItemCard,
    onItemClicked: (UiPantryIngredient) -> Unit
) {
    // Animation for color change
    val isInPantry = card.pantryIngredient.inPantry.value
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
    
    // Scale animation on click
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                isPressed = true
                onItemClicked(card.pantryIngredient)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100)
                isPressed = false
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = card.pantryIngredient.ingredient.name,
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
                    contentDescription = "In pantry",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 4.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add to pantry",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PantryCardList(
    pantry: List<UiPantryIngredient>,
    onItemClick: (UiPantryIngredient) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(pantry, key = { _, item -> item.ingredient.id }) { _, item ->
            PantryListItem(
                card = PantryItemCard(item),
                onItemClicked = onItemClick
            )
        }
    }
}