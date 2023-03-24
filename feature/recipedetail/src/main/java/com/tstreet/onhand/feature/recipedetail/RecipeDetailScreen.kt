package com.tstreet.onhand.feature.recipedetail

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import com.tstreet.onhand.core.ui.FullScreenProgressIndicator
import kotlinx.coroutines.flow.update

@Composable
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel,
    recipeId : Int?
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val recipe by viewModel.recipe.collectAsState()
    val recipeIdTest by viewModel.recipeId.collectAsState()

    // TODO: cleanup
    viewModel.onPageLoaded(recipeId!!)

    when {
        isLoading -> {
            FullScreenProgressIndicator()
        }
        else -> {
            AndroidView(factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()
                    loadUrl(recipe?.sourceUrl!!)
                }
            }, update = {
                it.loadUrl(recipe?.sourceUrl!!)
            })
        }
    }
}

