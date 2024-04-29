package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.scrollbar.DecorativeScrollbar
import com.rendox.grocerygenius.ui.components.scrollbar.scrollbarState
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupedLazyGroceryGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    groceryGroups: List<GroceryGroup>,
    numOfAdditionalItems: Int = 0,
    groceryItem: @Composable (Grocery) -> Unit,
    additionalItems: LazyGridScope.() -> Unit = {},
) {
    val itemsAvailable = remember(groceryGroups, numOfAdditionalItems) {
        val numOfGroceries = groceryGroups.sumOf { it.groceries.size }
        val numOfTitles = groceryGroups.sumOf {
            @Suppress("USELESS_CAST") // otherwise the compiler can not derive the type
            if (it.titleId != null) 1 else 0 as Int
        }
        val numOfDummies = 1
        numOfGroceries + numOfTitles + numOfDummies + numOfAdditionalItems
    }
    val scrollbarState = lazyGridState.scrollbarState(
        itemsAvailable = itemsAvailable,
    )
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val gridWidth = this.maxWidth
        val gridWidthPx = with(density) { gridWidth.roundToPx() }
        val horizontalArrangement = Arrangement.spacedBy(4.dp)
        val spacing = with(density) { horizontalArrangement.spacing.roundToPx() }

        LaunchedEffect(gridWidth) {
            println("GroupedLazyGroceryGrid: availableWidth = $gridWidthPx")
        }
        LaunchedEffect(spacing) {
            println("GroupedLazyGroceryGrid: spacing = $spacing")
        }
        LaunchedEffect(groceryGroups.firstOrNull()?.groceries) {
            println("GroupedLazyGroceryGrid: last grocery index = ${groceryGroups.firstOrNull()?.groceries?.lastIndex}")
        }

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
                            text = stringResource(id = R.string.purchased_groceries_group_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                items(
                    count = group.groceries.size,
                    key = { index -> group.groceries[index].productId },
                    contentType = { "Grocery" },
                ) { index ->
                    BoxWithConstraints(
                        modifier = Modifier
                            .aspectRatio(1F)
                            .animateItemPlacement()
                    ) {
                        val cellWidth = this.maxWidth
                        val cellWidthPx = with(density) { cellWidth.roundToPx() }
                        val numOfColumns =
                            ((gridWidthPx + spacing) / (cellWidthPx + spacing).toFloat()).roundToInt()
                        Box(
                            modifier = Modifier.groceryGridItemCornerRounding(
                                itemIndex = index,
                                numOfColumns = numOfColumns,
                                lastIndex = group.groceries.lastIndex,
                            )
                        ) {
                            println("GroupedLazyGroceryGrid: cell width = $cellWidthPx")
                            println("GroupedLazyGroceryGrid: numOfColumns = $numOfColumns")
                            groceryItem(group.groceries[index])
                        }
                    }
                }
            }

            additionalItems()
        }
        lazyGridState.DecorativeScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
        )
    }
}