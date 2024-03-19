package com.rendox.grocerygenius.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetDragHandle(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(vertical = 12.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            Modifier.size(
                width = 32.dp,
                height = 4.dp,
            )
        )
    }
}