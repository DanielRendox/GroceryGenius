package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.GroceryPresentation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupedLazyGroceryGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    groceryGroups: List<GroceryGroup>,
    groceryItem: @Composable (GroceryPresentation) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = lazyGridState,
        contentPadding = contentPadding,
        columns = GridCells.Adaptive(104.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Add dummy item to prevent automatic scroll when the first item is clicked
        // (this is a workaround for an internal bug in LazyVerticalGrid)
        item(
            key = "Dummy",
            span = { GridItemSpan(maxLineSpan) },
            contentType = "Dummy",
        ) {
            Spacer(modifier = Modifier.height(0.dp))
        }

        groceryGroups.forEachIndexed { groupIndex, group ->
            if (group.titleId != null) {
                item(
                    key = "Title$groupIndex",
                    span = { GridItemSpan(maxLineSpan) },
                    contentType = "Title",
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 8.dp)
                            .animateItemPlacement(),
                        text = stringResource(id = R.string.not_purchased_groceries_group_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            items(
                items = group.groceries,
                key = { it.productId },
                contentType = { "Grocery" },
            ) { grocery ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1F)
                        .animateItemPlacement()
                ) {
                    groceryItem(grocery)
                }
            }
        }
    }
}