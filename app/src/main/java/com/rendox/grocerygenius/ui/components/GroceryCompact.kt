package com.rendox.grocerygenius.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.ui.theme.CornerRoundingDefault

@Composable
fun GroceryCompact(
    modifier: Modifier = Modifier,
    title: String,
    backgroundColor: Color = Color.Unspecified,
    icon: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = CornerRoundingDefault,
        color = backgroundColor,
    ) {
        Row(
            modifier = modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}