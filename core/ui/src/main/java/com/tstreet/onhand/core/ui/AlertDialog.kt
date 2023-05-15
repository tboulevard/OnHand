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
    dismissButtonText: String = "Dismiss",
    confirmButtonText: String = "Confirm",
    showConfirmButton: Boolean = false,
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
                    Text(dismissButtonText)
                }
            },
            confirmButton = {
                if (showConfirmButton) {
                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        }
                    ) {
                        Text(confirmButtonText)
                    }
                }
            },
        )
    }
}
