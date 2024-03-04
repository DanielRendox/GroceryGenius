package com.rendox.grocerygenius.feature.grocery_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.ToolbarState
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffold
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState

@Composable
fun GroceryListRoute(
    modifier: Modifier = Modifier,
    viewModel: GroceryListScreenViewModel = viewModel(),
) {
    val groceries by viewModel.groceriesFlow.collectAsState()

    GroceryListScreen(
        modifier = modifier,
        listName = "Grocery List",
        groceries = groceries,
        onGroceryItemClick = viewModel::toggleItemPurchased,
    )
}

@Composable
private fun GroceryListScreen(
    modifier: Modifier = Modifier,
    listName: String,
    groceries: Map<Boolean, List<Grocery>>,
    onGroceryItemClick: (Grocery) -> Unit,
) {
    val collapsedToolbarHeight = 64.dp
    val expandedToolbarHeight = 112.dp
    val toolbarHeightRange = with(LocalDensity.current) {
        collapsedToolbarHeight.roundToPx()..expandedToolbarHeight.roundToPx()
    }
    val toolbarState = rememberExitUntilCollapsedToolbarState(toolbarHeightRange)
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val scrollState: CollapsingToolbarScaffoldScrollableState = remember {
        object : CollapsingToolbarScaffoldScrollableState, ScrollableState by lazyGridState {
            override val firstVisibleItemIndex: Int
                get() = lazyGridState.firstVisibleItemIndex
            override val firstVisibleItemScrollOffset: Int
                get() = lazyGridState.firstVisibleItemScrollOffset
        }
    }

    CollapsingToolbarScaffold(
        modifier = modifier,
        nestedScrollConnection = CollapsingToolbarNestedScrollConnection(
            toolbarState = toolbarState,
            scrollState = scrollState,
            coroutineScope = coroutineScope,
        ),
        toolbar = {
            GroceryListCollapsingToolbar(
                listName = listName,
                toolbarHeightRange = toolbarHeightRange,
                toolbarState = toolbarState,
            )
        },
        toolbarHeightRange = toolbarHeightRange,
    ) { bottomPadding ->
        LazyGroceryGrid(
            lazyGridState = lazyGridState,
            groceries = groceries,
            onGroceryItemClick = onGroceryItemClick,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = bottomPadding + 16.dp,
            ),
            columns = GridCells.Adaptive(104.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        )
    }
}

@Composable
private fun GroceryListCollapsingToolbar(
    modifier: Modifier = Modifier,
    listName: String,
    toolbarHeightRange: IntRange,
    toolbarState: ToolbarState,
) {
    val expandedTitleStyle = MaterialTheme.typography.headlineSmall
    CollapsingToolbar(
        modifier = modifier.graphicsLayer {
            translationY = toolbarState.offset
        },
        titleExpanded = {
            Text(
                text = listName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = expandedTitleStyle.copy(
                    textMotion = TextMotion.Animated
                ),
            )
        },
        expandedTitleFontSize = expandedTitleStyle.fontSize,
        titleBottomPadding = 24.dp,
        toolbarState = toolbarState,
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        toolbarHeightRange = toolbarHeightRange,
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyGroceryGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    contentPadding: PaddingValues,
    columns: GridCells,
    horizontalArrangement: Arrangement.Horizontal,
    verticalArrangement: Arrangement.Vertical,
    groceries: Map<Boolean, List<Grocery>>,
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

        for (group in groceries) {
            if (group.key) {
                item(
                    key = -2,
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


