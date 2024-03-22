package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.model.Grocery

@Composable
fun LazyGroceryGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    groceries: List<Grocery>,
    groceryItem: @Composable (Grocery) -> Unit,
    customProduct: (@Composable () -> Unit)? = null,
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
        items(
            items = groceries,
            key = { it.productId },
            contentType = { "Grocery" },
        ) { grocery ->
            Box(modifier = Modifier.aspectRatio(1F)) {
                groceryItem(grocery)
            }
        }
        if (customProduct != null) {
            item(
                key = "CustomProduct",
                contentType = { "Grocery" },
            ) {
                Box(modifier = Modifier.aspectRatio(1F)) {
                    customProduct()
                }
            }
        }
    }
}

