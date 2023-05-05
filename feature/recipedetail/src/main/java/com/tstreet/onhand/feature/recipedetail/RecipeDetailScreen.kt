package com.tstreet.onhand.feature.recipedetail

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
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
    // TODO: research collectAsstatewithlifecycle instead...
    val uiState by viewModel.recipeDetailUiState.collectAsState()
    val openErrorDialog = viewModel.showErrorDialog.collectAsState()

    when (val state = uiState) {
        is Loading -> {
            OnHandProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is Success -> {
            AndroidView(
                factory = {
                    WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                    }
                }, update = {
                    // Smart cast to 'RecipeDetailUiState.Success' is impossible, because 'uiState'
                    // is a property that has open or custom getter
                    it.loadUrl(state.recipeDetail.sourceUrl)
                })
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

