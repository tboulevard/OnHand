package com.tstreet.onhand.feature.recipedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.RecipeDetailUiState.*

@Composable
fun RecipeDetailScreen(
    navController: NavHostController,
    viewModel: RecipeDetailViewModel
) {
    val uiState by viewModel.recipeDetailUiState.collectAsStateWithLifecycle()
    val openErrorDialog = viewModel.showErrorDialog.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is Success -> {
            Column {
                Text(text = state.recipe?.preview?.title ?: "No title provided")
                Text(text = "You have: ${state.recipe?.preview?.usedIngredients?.map { it.ingredient.name }}")
                Text(text = "You are missing: ${state.recipe?.preview?.missedIngredients?.map { it.ingredient.name }}")
                Text(text = state.recipe?.detail?.instructions ?: "No instructions provided")
            }
        }
        is Error -> {
            if (openErrorDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.dismissErrorDialog()
                        navController.popBackStack()
                    },
                    title = {
                        Text("Error")
                    },
                    text = {
                        Text(state.message)
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

