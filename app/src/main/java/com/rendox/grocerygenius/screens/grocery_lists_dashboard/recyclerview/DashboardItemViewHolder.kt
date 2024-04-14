package com.rendox.grocerygenius.screens.grocery_lists_dashboard.recyclerview

import androidx.compose.foundation.clickable
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
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

class DashboardItemViewHolder(
    private val composeView: ComposeView,
    private val onDrag: (ViewHolder) -> Unit,
    private val onViewClicked: (String) -> Unit,
) : ViewHolder(composeView) {

    fun bind(groceryList: GroceryList) {
        composeView.setContent {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                list = groceryList,
                onDrag = { onDrag(this) },
                onClick = { onViewClicked(groceryList.id) },
            )
        }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    list: GroceryList,
    onDrag: () -> Unit = {},
    onClick: () -> Unit = {},
) = ElevatedCard(modifier = modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(end = 50.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = list.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(
                    id = R.string.dashboard_item_num_of_groceries_title,
                    list.numOfGroceries,
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
                list = GroceryList(
                    id = "1",
                    name = "Sample List",
                    numOfGroceries = 5,
                )
            )
        }
    }
}