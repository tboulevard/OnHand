package com.tstreet.onhand.core.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.res.stringResource
import com.tstreet.onhand.core.common.R.string.confirm
import com.tstreet.onhand.core.common.R.string.dismiss

@Composable
fun OnHandAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = { },
    dismissButtonText: String = stringResource(dismiss),
    confirmButtonText: String = stringResource(confirm),
    showConfirmButton: Boolean = false,
    state: AlertDialogState,
) {
    if (state.shouldDisplay) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(state.title)
            },
            text = {
                Text(state.message)
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

class AlertDialogState(
    val shouldDisplay: Boolean,
    val title: String,
    val message: String
) {
    companion object {
        fun dismissed() = AlertDialogState(
            shouldDisplay = false,
            title = "",
            message = ""
        )

        fun displayed(
            title: String,
            message: String
        ) = AlertDialogState(
            shouldDisplay = true,
            title = title,
            message = message
        )
    }
}
