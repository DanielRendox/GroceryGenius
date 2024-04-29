package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.model.Grocery
import kotlin.math.roundToInt

@Composable
fun LazyGroceryGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    groceries: List<Grocery>,
    groceryItem: @Composable (Grocery) -> Unit,
    customProduct: (@Composable () -> Unit)? = null,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val gridWidth = this.maxWidth
        val gridWidthPx = with(density) { gridWidth.roundToPx() }
        val horizontalArrangement = Arrangement.spacedBy(4.dp)
        val spacing = with(density) { horizontalArrangement.spacing.roundToPx() }

        LazyVerticalGrid(
            state = lazyGridState,
            contentPadding = contentPadding,
            columns = GridCells.Adaptive(104.dp),
            horizontalArrangement = horizontalArrangement,
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
                count = groceries.size,
                key = { index -> groceries[index].productId },
                contentType = { "Grocery" },
            ) { index ->
                BoxWithConstraints(modifier = Modifier.aspectRatio(1F)) {
                    val cellWidth = this.maxWidth
                    val cellWidthPx = with(density) { cellWidth.roundToPx() }
                    val numOfColumns =
                        ((gridWidthPx + spacing) / (cellWidthPx + spacing).toFloat()).roundToInt()
                    Box(
                        modifier = Modifier.groceryGridItemCornerRounding(
                            itemIndex = index,
                            numOfColumns = numOfColumns,
                            lastIndex = groceries.lastIndex + if (customProduct != null) 1 else 0,
                        )
                    ) {
                        groceryItem(groceries[index])
                    }
                }
            }
            if (customProduct != null) {
                item(
                    key = "CustomProduct",
                    contentType = { "Grocery" },
                ) {
                    BoxWithConstraints(modifier = Modifier.aspectRatio(1F)) {
                        val cellWidth = this.maxWidth
                        val cellWidthPx = with(density) { cellWidth.roundToPx() }
                        val numOfColumns =
                            ((gridWidthPx + spacing) / (cellWidthPx + spacing).toFloat()).roundToInt()
                        Box(
                            modifier = Modifier.groceryGridItemCornerRounding(
                                itemIndex = groceries.lastIndex + 1,
                                numOfColumns = numOfColumns,
                                lastIndex = groceries.lastIndex + 1,
                            )
                        ) {
                            customProduct()
                        }
                    }
                }
            }
        }
    }
}

