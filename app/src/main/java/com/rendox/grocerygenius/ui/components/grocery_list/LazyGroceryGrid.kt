package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyGroceryGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    contentPadding: PaddingValues,
    columns: GridCells,
    horizontalArrangement: Arrangement.Horizontal,
    verticalArrangement: Arrangement.Vertical,
    groceryGroups: List<GroceryGroup>,
    onGroceryItemClick: (Grocery) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = lazyGridState,
        contentPadding = contentPadding,
        columns = columns,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
    ) {
        // Add dummy item to prevent automatic scroll when the first item is clicked
        // (this is a workaround for an internal bug in LazyVerticalGrid)

        item(
            key = -1,
            span = { GridItemSpan(maxLineSpan) },
            contentType = "Dummy",
        ) {
            Spacer(modifier = Modifier.height(0.dp))
        }

        groceryGroups.forEachIndexed { groupIndex, group ->
            if (group.titleId != null) {
                item(
                    // negative indices as positive ones are occupied by items
                    // subtract 1 as indices start from zero
                    // subtract 1 again as -1 is occupied by the dummy item
                    key = -groupIndex - 2,
                    span = { GridItemSpan(maxLineSpan) },
                    contentType = "Title"
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .animateItemPlacement(),
                        text = stringResource(id = R.string.not_purchased_groceries_group_title),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            items(
                items = group.groceries,
                key = { it.id.toInt() },
                contentType = { "Grocery" }
            ) { grocery ->
                GroceryCard(
                    modifier = Modifier
                        .aspectRatio(1F)
                        .animateItemPlacement(),
                    grocery = grocery,
                    onClick = {
                        onGroceryItemClick(grocery)
                    },
                )
            }
        }
    }
}

@Composable
private fun GroceryCard(
    modifier: Modifier = Modifier,
    grocery: Grocery,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (grocery.purchased) {
            MaterialTheme.colorScheme.surfaceColorAtElevation(elevation = 1.dp)
        } else {
            MaterialTheme.colorScheme.primaryContainer
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Name: ${grocery.name}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "ID: ${grocery.id}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "Purchased: ${grocery.purchased}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}