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
import com.tstreet.onhand.feature.recipedetail.RecipeDetailUiState.*

@Composable
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel
) {
    // TODO: research collectasstatewithlifecycle instead...
    val uiState by viewModel.recipeDetailUiState.collectAsState()

    when (uiState) {
        Loading -> {
            println("[OnHand] recompose: loading")
            FullScreenProgressIndicator()
        }
        is Success -> {
            println("[OnHand] recompose: success")
            AndroidView(factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()
                    // TODO: to get around this error:
                    // Smart cast to 'RecipeDetailUiState.Success' is impossible, because 'uiState'
                    // is a property that has open or custom getter
                    loadUrl((uiState as Success).recipeDetail.sourceUrl)
                }
            }, update = {
                it.loadUrl((uiState as Success).recipeDetail.sourceUrl)
            })
        }
        else -> {
            println("recompose: else")
            FullScreenErrorMessage(msg = "error - fill in details later")
        }
    }
}

