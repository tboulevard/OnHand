package com.tstreet.onhand.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text

@Composable
fun FullScreenErrorMessage(msg : String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = msg,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}