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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.common.R.string.dismiss
import com.tstreet.onhand.core.common.R.string.error_title
import com.tstreet.onhand.core.common.R.string.ingredients_string
import com.tstreet.onhand.core.common.R.string.instructions_title
import com.tstreet.onhand.core.common.R.string.navigate_back_content_description
import com.tstreet.onhand.core.common.R.string.no_instructions_provided
import com.tstreet.onhand.core.common.R.string.no_title_provided
import com.tstreet.onhand.core.common.R.string.recipe_details_title
import com.tstreet.onhand.core.common.R.string.step_format
import com.tstreet.onhand.core.common.R.string.you_are_missing
import com.tstreet.onhand.core.common.R.string.you_have
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.model.ui.RecipeDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavHostController,
    viewModel: RecipeDetailViewModel
) {
    val uiState by viewModel.recipeDetailUiState.collectAsStateWithLifecycle()
    val openErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(recipe_details_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(navigate_back_content_description)
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
                    if (openErrorDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                viewModel.dismissErrorDialog()
                                navController.popBackStack()
                            },
                            title = {
                                Text(stringResource(error_title), style = MaterialTheme.typography.titleLarge)
                            },
                            text = {
                                Text(state.message, style = MaterialTheme.typography.bodyMedium)
                            },
                            dismissButton = {
                                Button(onClick = {
                                    viewModel.dismissErrorDialog()
                                    navController.popBackStack()
                                }) {
                                    Text(stringResource(dismiss))
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
            text = state.recipe?.preview?.title ?: stringResource(no_title_provided),
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
                    text = stringResource(ingredients_string),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Available ingredients
                Text(
                    text = stringResource(you_have),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.recipe?.preview?.usedIngredients?.forEach { ingredient ->
                    Text(
                        text = "• ${ingredient.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Missing ingredients
                Text(
                    text = stringResource(you_are_missing),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.recipe?.preview?.missedIngredients?.forEach { ingredient ->
                    Text(
                        text = "• ${ingredient.name}",
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
                    text = stringResource(instructions_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                val noInstructionsText = stringResource(no_instructions_provided)
                val instructions = state.recipe?.detail?.instructions ?: noInstructionsText
                val steps = remember {
                    if (instructions == noInstructionsText) {
                        listOf(noInstructionsText)
                    } else {
                        // Split instructions into steps based on numbering or line breaks
                        // TODO: Move to viewmodel at least
                        instructions.split(Regex("\\d+\\.\\s|\\n+"))
                            .filter { it.trim().isNotEmpty() }
                            .map { it.trim() }
                    }
                }
                
                val completedSteps = remember { mutableStateListOf<Int>() }
                
                steps.forEachIndexed { index, step ->
                    if (step != noInstructionsText) {
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
            text = stringResource(step_format, stepNumber, stepText),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isCompleted) 
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}