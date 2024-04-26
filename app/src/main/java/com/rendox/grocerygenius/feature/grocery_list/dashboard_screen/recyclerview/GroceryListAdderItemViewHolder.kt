package com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.recyclerview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

class GroceryListAdderItemViewHolder(
    private val composeView: ComposeView,
    private val onViewClicked: () -> Unit,
) : RecyclerView.ViewHolder(composeView) {

    fun bind() {
        composeView.setContent {
            AdderItem(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 16.dp,
                    ),
                onClick = onViewClicked
            )
        }
    }
}

@Composable
private fun AdderItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                )
                Text(
                    text = stringResource(R.string.add_grocery_list_button_description),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun PreviewListItem() {
    GroceryGeniusTheme {
        Surface {
            AdderItem(
                modifier = Modifier
                    .padding(16.dp)
                    .width(400.dp),
            )
        }
    }
}