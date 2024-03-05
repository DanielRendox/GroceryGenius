package com.rendox.grocerygenius.feature.grocery_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.ToolbarState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
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

    val nestedScrollConnection = remember {
        CollapsingToolbarNestedScrollConnection(
            toolbarState = toolbarState,
            scrollState = scrollState,
            coroutineScope = coroutineScope,
        )
    }

    val systemBarOffset = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val searchBarFocusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current
    val sheetStateIsExpanded =
        scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
    val sheetIsExpanding =
        scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded
    println("GroceryListScreen sheet targetValue = ${scaffoldState.bottomSheetState.targetValue}")
    LaunchedEffect(sheetStateIsExpanded, sheetIsExpanding) {
        if (!sheetIsExpanding) {
            focusManager.clearFocus()
        } else if (sheetStateIsExpanded) {
            searchBarFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars
            ),
        sheetContent = {
            if (sheetStateIsExpanded) {
                BackHandler {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                    }
                }
            }

            BottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp * 0.75F.dp)
                    .windowInsetsPadding(
                        WindowInsets.navigationBars
                    ),
                onSearchBarFocusChanged = { focusState ->
                    coroutineScope.launch {
                        if (focusState.isFocused) {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                },
                fabOnClick = {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                },
                cancelButtonOnClick = {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                    }
                },
                searchBarFocusRequester = searchBarFocusRequester,
                showCancelButtonInsteadOfFab = sheetStateIsExpanded,
            )
        },
        sheetDragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    Modifier.size(
                        width = 32.dp,
                        height = 4.dp,
                    )
                )
            }
        },
        sheetPeekHeight = 100.dp + navigationBarHeight,
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        val topAppBarOffset by animateFloatAsState(
            targetValue = if (sheetIsExpanding) toolbarState.height else 0F,
            label = "animateFloatAsState",
            animationSpec = tween(
                easing = LinearEasing,
                durationMillis = 200,
            ),
        )
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyGroceryGrid(
                modifier = Modifier
                    .graphicsLayer {
                        translationY =
                            toolbarState.height + toolbarState.offset - topAppBarOffset
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                coroutineScope.coroutineContext.cancelChildren()
                            }
                        )
                    },
                lazyGridState = lazyGridState,
                groceries = groceries,
                onGroceryItemClick = onGroceryItemClick,
                contentPadding = PaddingValues(
                    top = systemBarOffset,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp,
                ),
                columns = GridCells.Adaptive(104.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            )

            GroceryListCollapsingToolbar(
                modifier = Modifier
                    .systemBarsPadding()
                    .graphicsLayer {
                        translationY = toolbarState.offset - topAppBarOffset
                    },
                listName = listName,
                toolbarHeightRange = toolbarHeightRange,
                toolbarState = toolbarState,
            )
        }

        val scrim = MaterialTheme.colorScheme.scrim
        if (sheetIsExpanding) {
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = scrim.copy(alpha = 0.32F))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.partialExpand()
                        }
                    }
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    modifier: Modifier = Modifier,
    showCancelButtonInsteadOfFab: Boolean,
    searchBarFocusRequester: FocusRequester,
    onSearchBarFocusChanged: (FocusState) -> Unit,
    fabOnClick: () -> Unit,
    cancelButtonOnClick: () -> Unit,
) {
    Column(modifier = modifier) {
        var searchInput by remember { mutableStateOf("") }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            TextField(
                modifier = Modifier
                    .weight(1F)
                    .onFocusChanged(
                        onFocusChanged = onSearchBarFocusChanged
                    )
                    .focusRequester(focusRequester = searchBarFocusRequester),
                value = searchInput,
                onValueChange = { searchInput = it },
                shape = RoundedCornerShape(20),
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (showCancelButtonInsteadOfFab) {
                    TextButton(
                        onClick = cancelButtonOnClick
                    ) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clickable(onClick = fabOnClick)
                            .aspectRatio(1F)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(
                                id = R.string.add_grocery_fab_description
                            ),
                        )
                    }
                }
            }
        }
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

