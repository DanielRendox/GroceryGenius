package com.rendox.grocerygenius.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rendox.grocerygenius.R

@Composable
fun DeleteConfirmationDialog(
    modifier: Modifier = Modifier,
    bodyText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "${stringResource(R.string.delete)}?")
        },
        text = {
            Text(text = bodyText)
        },
        confirmButton = {
            FilledTonalButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
    )
}