package com.tstreet.onhand.core.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.AlertDialog

@Composable
fun OnHandAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = { },
    titleText: String,
    bodyText: String,
    buttonText: String = "Ok",
    shouldDisplay: Boolean = true
) {
    if (shouldDisplay) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(titleText)
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
            confirmButton = { onConfirm.invoke() },
        )
    }
}
