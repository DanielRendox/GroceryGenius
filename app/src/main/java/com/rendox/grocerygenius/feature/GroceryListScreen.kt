package com.rendox.grocerygenius.feature

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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery

@Composable
fun GroceryListRoute(
    modifier: Modifier = Modifier,
    viewModel: GroceryListScreenViewModel = viewModel(),
) {
    val groceries by viewModel.groceriesFlow.collectAsState()

    GroceryListScreen(
        modifier = modifier,
        groceries = groceries,
        onGroceryItemClick = viewModel::toggleItemPurchased,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroceryListScreen(
    modifier: Modifier = Modifier,
    groceries: Map<Boolean, List<Grocery>>,
    onGroceryItemClick: (Grocery) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(104.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Add dummy item to prevent automatic scroll when the first item is clicked
        // (this is a workaround for an internal bug in LazyVerticalGrid)
        item (
            key = -1,
            span = { GridItemSpan(maxLineSpan) },
            contentType = "Dummy",
        ) {
            Spacer(modifier = Modifier.height(0.dp))
        }

        for (group in groceries) {
            if (group.key) {
                item(
                    key = -2,
                    span = { GridItemSpan(maxLineSpan) },
                    contentType = "Title"
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp).animateItemPlacement(),
                        text = stringResource(id = R.string.not_purchased_groceries_group_title),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            items(
                items = group.value,
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


