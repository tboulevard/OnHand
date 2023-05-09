package com.tstreet.onhand.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text

@Composable
fun FullScreenErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = message,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

class ErrorDialogState(
    val shouldDisplay: Boolean,
    val message: String = ""
) {
    companion object {
        fun dismissed() = ErrorDialogState(shouldDisplay = false)
        fun displayed(message: String) = ErrorDialogState(
            shouldDisplay = true,
            message = message
        )
    }
}
