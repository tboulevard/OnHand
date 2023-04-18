package com.tstreet.onhand.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnHandScreenHeader(text: String) {
    Text(
        modifier = Modifier
            .padding(12.dp),
        text = text,
        style = MaterialTheme.typography.displayMedium
    )
}