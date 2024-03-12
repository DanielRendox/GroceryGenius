package com.rendox.grocerygenius.feature.grocery_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.ToolbarState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState
import com.rendox.grocerygenius.ui.helpers.ObserveUiEvent
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
    val bottomSheetContentState = rememberAddGroceryBottomSheetContentState()

    val focusManager = LocalFocusManager.current
    val sheetStateIsExpanded =
        scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
    val sheetIsExpanding =
        scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded

    LaunchedEffect(sheetStateIsExpanded, sheetIsExpanding) {
        bottomSheetContentState.showCancelButtonInsteadOfFab = sheetIsExpanding
        bottomSheetContentState.useExpandedPlaceholderText = sheetIsExpanding
        if (!sheetIsExpanding) {
            focusManager.clearFocus()
            bottomSheetContentState.clearSearchInput()
        } else if (sheetStateIsExpanded) {
            searchBarFocusRequester.requestFocus()
        }
    }

    ObserveUiEvent(bottomSheetContentState.expandBottomSheetEvent) { expand ->
        if (expand) {
            scaffoldState.bottomSheetState.expand()
        } else {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    // keep the sheet always on screen
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

            AddGroceryBottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp * 0.75F.dp)
                    .windowInsetsPadding(
                        WindowInsets.navigationBars
                    ),
                state = bottomSheetContentState,
                searchBarFocusRequester = searchBarFocusRequester,
            )
        },
        sheetDragHandle = {
            SheetDragHandle(modifier = Modifier.padding(vertical = 12.dp))
        },
        sheetPeekHeight = 100.dp + navigationBarHeight,
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        val hideToolbarOffset by animateFloatAsState(
            targetValue = if (sheetIsExpanding) {
                toolbarState.height + WindowInsets.systemBars.getTop(LocalDensity.current)
            } else {
                0F
            },
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
            GroceryList(
                modifier = Modifier
                    .graphicsLayer {
                        translationY =
                            toolbarState.height + toolbarState.offset - hideToolbarOffset
                    }
                    // stop scroll when the user taps on screen
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                coroutineScope.coroutineContext.cancelChildren()
                            }
                        )
                    },
                groceries = groceries,
                lazyGridState = lazyGridState,
                onGroceryItemClick = onGroceryItemClick,
                contentPadding = PaddingValues(
                    top = systemBarOffset,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = collapsedToolbarHeight + 16.dp,
                ),
            )

            GroceryListCollapsingToolbar(
                modifier = Modifier.graphicsLayer {
                    translationY = toolbarState.offset - hideToolbarOffset
                },
                listName = listName,
                toolbarHeightRange = toolbarHeightRange,
                toolbarState = toolbarState,
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        if (sheetIsExpanding) {
            Scrim(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.partialExpand()
                        }
                    },
                )
            )
        }
    }
}

@Composable
private fun GroceryList(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    groceries: Map<Boolean, List<Grocery>>,
    onGroceryItemClick: (Grocery) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyGroceryGrid(
        modifier = modifier,
        lazyGridState = lazyGridState,
        groceries = groceries,
        onGroceryItemClick = onGroceryItemClick,
        contentPadding = contentPadding,
        columns = GridCells.Adaptive(104.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    )
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