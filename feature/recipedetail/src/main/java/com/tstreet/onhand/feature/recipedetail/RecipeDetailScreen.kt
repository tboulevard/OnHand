package com.tstreet.onhand.feature.recipedetail

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.tstreet.onhand.core.ui.OnHandProgressIndicator
import com.tstreet.onhand.core.ui.RecipeDetailUiState.*

@Composable
fun RecipeDetailScreen(
    navController: NavHostController,
    viewModel: RecipeDetailViewModel
) {
    // TODO: research collectAsStateWithLifecycle instead...
    val uiState by viewModel.recipeDetailUiState.collectAsState()
    val openErrorDialog = viewModel.showErrorDialog.collectAsState()

    when (val state = uiState) {
        is Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is Success -> {
            Column {
                // TODO: ingredients...
                Text(text = state.recipeDetail.title)
                Text(text = state.recipeDetail.instructions ?: "No instructions provided.")
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

