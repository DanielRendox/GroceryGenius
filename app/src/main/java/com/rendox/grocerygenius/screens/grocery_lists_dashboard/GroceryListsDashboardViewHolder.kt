package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

class GroceryListsDashboardViewHolder(
    private val composeView: ComposeView
) : RecyclerView.ViewHolder(composeView) {
    fun bind(
        groceryList: GroceryListsDashboardItem,
    ) {
        composeView.setContent {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                list = groceryList
            )
        }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    list: GroceryListsDashboardItem,
) = ElevatedCard(modifier = modifier) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(list.name, style = MaterialTheme.typography.titleLarge)
        Text(text = "${list.itemCount} items")
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