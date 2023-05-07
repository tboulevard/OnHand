package com.tstreet.onhand.core.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.AlertDialog

@Composable
fun OnHandAlertDialog(
    onDismiss: () -> Unit,
    bodyText: String,
    buttonText: String = "Ok"
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text("Error")
        },
        text = {
            Text(bodyText)
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text(buttonText)
            }
        },
        confirmButton = { },
    )
}