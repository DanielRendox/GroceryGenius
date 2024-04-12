package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

class GroceryListsDashboardViewHolder(
    private val composeView: ComposeView,
    private val onDrag: (ViewHolder) -> Unit,
) : ViewHolder(composeView) {
    fun bind(
        groceryList: GroceryListsDashboardItem,
    ) {
        composeView.setContent {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                list = groceryList,
                onDrag = { onDrag(this) }
            )
        }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    list: GroceryListsDashboardItem,
    onDrag: () -> Unit = {},
) = ElevatedCard(modifier = modifier) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(end = 50.dp)) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(
                    id = R.string.dashboard_item_num_of_groceries_title,
                    list.itemCount,
                )
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .draggable(
                    state = rememberDraggableState { onDrag() },
                    startDragImmediately = true,
                    orientation = Orientation.Vertical,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(id = R.drawable.baseline_drag_handle_24),
                contentDescription = null,
            )
        }
    }
}

@Composable
@Preview
fun PreviewListItem() {
    GroceryGeniusTheme {
        Surface {
            ListItem(
                modifier = Modifier.width(400.dp),
                list = GroceryListsDashboardItem(
                    id = "1",
                    name = "Sample List",
                    itemCount = 5,
                    items = emptyList()
                )
            )
        }
    }
}