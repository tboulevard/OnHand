package com.tstreet.onhand.feature.recipedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.model.ui.RecipeDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavHostController,
    viewModel: RecipeDetailViewModel
) {
    val uiState by viewModel.recipeDetailUiState.collectAsStateWithLifecycle()
    val openErrorDialog = viewModel.showErrorDialog.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val state = uiState) {
                is RecipeDetailUiState.Loading -> {
                    OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
                }
                is RecipeDetailUiState.Success -> {
                    RecipeDetailContent(state)
                }
                is RecipeDetailUiState.Error -> {
                    if (openErrorDialog.value) {
                        AlertDialog(
                            onDismissRequest = {
                                viewModel.dismissErrorDialog()
                                navController.popBackStack()
                            },
                            title = {
                                Text("Error", style = MaterialTheme.typography.titleLarge)
                            },
                            text = {
                                Text(state.message, style = MaterialTheme.typography.bodyMedium)
                            },
                            dismissButton = {
                                Button(onClick = {
                                    viewModel.dismissErrorDialog()
                                    navController.popBackStack()
                                }) {
                                    Text("Dismiss")
                                }
                            },
                            confirmButton = { },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeDetailContent(state: RecipeDetailUiState.Success) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = state.recipe?.preview?.title ?: "No title provided",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Ingredients section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Available ingredients
                Text(
                    text = "You have:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.recipe?.preview?.usedIngredients?.forEach { ingredient ->
                    Text(
                        text = "• ${ingredient.ingredient.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Missing ingredients
                Text(
                    text = "You are missing:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.recipe?.preview?.missedIngredients?.forEach { ingredient ->
                    Text(
                        text = "• ${ingredient.ingredient.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Instructions section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                val instructions = state.recipe?.detail?.instructions ?: "No instructions provided"
                val steps = remember {
                    if (instructions == "No instructions provided") {
                        listOf("No instructions provided")
                    } else {
                        // Split instructions into steps based on numbering or line breaks
                        instructions.split(Regex("\\d+\\.\\s|\\n+"))
                            .filter { it.trim().isNotEmpty() }
                            .map { it.trim() }
                    }
                }
                
                val completedSteps = remember { mutableStateListOf<Int>() }
                
                steps.forEachIndexed { index, step ->
                    if (step != "No instructions provided") {
                        InstructionStep(
                            stepNumber = index + 1,
                            stepText = step,
                            isCompleted = completedSteps.contains(index),
                            onToggleCompleted = { isChecked ->
                                if (isChecked) {
                                    completedSteps.add(index)
                                } else {
                                    completedSteps.remove(index)
                                }
                            }
                        )
                        
                        if (index < steps.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(modifier = Modifier.padding(start = 56.dp))
                        }
                    } else {
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionStep(
    stepNumber: Int,
    stepText: String,
    isCompleted: Boolean,
    onToggleCompleted: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = isCompleted,
            onCheckedChange = onToggleCompleted
        )
        
        Text(
            text = "Step $stepNumber: $stepText",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isCompleted) 
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}