package com.tstreet.onhand.feature.recipedetail

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import com.tstreet.onhand.core.ui.FullScreenErrorMessage
import com.tstreet.onhand.core.ui.FullScreenProgressIndicator
import com.tstreet.onhand.core.ui.RecipeDetailUiState.*

@Composable
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel
) {
    // TODO: research collectAsstatewithlifecycle instead...
    val uiState by viewModel.recipeDetailUiState.collectAsState()

    when (val state = uiState) {
        is Loading -> {
            FullScreenProgressIndicator()
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
            FullScreenErrorMessage(message = state.message)
        }
    }
}

