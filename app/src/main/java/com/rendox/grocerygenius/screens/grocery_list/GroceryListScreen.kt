package com.rendox.grocerygenius.screens.grocery_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.screens.edit_grocery.EditGroceryScreen
import com.rendox.grocerygenius.screens.edit_grocery.EditGroceryScreenIntent
import com.rendox.grocerygenius.screens.edit_grocery.EditGroceryViewModel
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.AddGroceryBottomSheetContent
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.AddGroceryBottomSheetState
import com.rendox.grocerygenius.screens.grocery_list.add_grocery_bottom_sheet.rememberAddGroceryBottomSheetState
import com.rendox.grocerygenius.ui.components.BottomSheetDragHandle
import com.rendox.grocerygenius.ui.components.Scrim
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.ToolbarState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState
import com.rendox.grocerygenius.ui.components.grocery_list.GroceryGroup
import com.rendox.grocerygenius.ui.components.grocery_list.GroupedLazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGridItem
import com.rendox.grocerygenius.ui.components.grocery_list.groceryListItemColors
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListRoute(
    modifier: Modifier = Modifier,
    groceryListViewModel: GroceryListViewModel = viewModel(),
) {
    val groceryGroups by groceryListViewModel.groceryGroupsFlow.collectAsStateWithLifecycle()
    val screenState by groceryListViewModel.screenStateFlow.collectAsStateWithLifecycle()

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

    val addBottomSheetState = rememberStandardBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(addBottomSheetState)
    val addGroceryBottomSheetState = rememberAddGroceryBottomSheetState(addBottomSheetState)

    LaunchedEffect(addGroceryBottomSheetState.sheetIsCollapsing) {
        groceryListViewModel.onIntent(GroceryListScreenIntent.OnAddGroceryBottomSheetCollapsing)
    }

    val editBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editGroceryScreenIsVisible by rememberSaveable { mutableStateOf(false) }
    val productIdState = rememberSaveable { mutableStateOf<String?>(null) }

    GroceryListScreen(
        modifier = modifier,
        groceryGroups = groceryGroups,
        searchQuery = groceryListViewModel.searchQuery,
        toolbarState = toolbarState,
        toolbarHeightRange = toolbarHeightRange,
        screenState = screenState,
        nestedScrollConnection = nestedScrollConnection,
        onIntent = groceryListViewModel::onIntent,
        scaffoldState = scaffoldState,
        addGroceryBottomSheetState = addGroceryBottomSheetState,
        showEditGroceryBottomSheet = {
            productIdState.value = it
            editGroceryScreenIsVisible = true
        },
        lazyGridState = lazyGridState,
        scrimIsShown = addGroceryBottomSheetState.sheetIsExpanding ||
                editBottomSheetState.targetValue == SheetValue.Expanded,
        toolbarIsHidden = addGroceryBottomSheetState.sheetIsExpanding,
    )

    val productId = productIdState.value
    if (editGroceryScreenIsVisible && productId != null) {
        val editGroceryViewModel: EditGroceryViewModel = viewModel()
        LaunchedEffect(productId) {
            editGroceryViewModel.onIntent(
                EditGroceryScreenIntent.OnEditOtherGrocery(
                    productId = productId,
                    groceryListId = groceryListViewModel.groceryListId,
                )
            )
        }

        val editGroceryScreenState by editGroceryViewModel.screenState.collectAsStateWithLifecycle()
        EditGroceryScreen(
            modifier = Modifier.fillMaxSize(),
            screenState = editGroceryScreenState,
            editGroceryDescription = editGroceryViewModel.editGroceryDescription,
            editBottomSheetState = editBottomSheetState,
            hideBottomSheet = {
                coroutineScope
                    .launch { editBottomSheetState.hide() }
                    .invokeOnCompletion {
                        if (!editBottomSheetState.isVisible) {
                            editGroceryScreenIsVisible = false
                        }
                    }
            },
            onIntent = editGroceryViewModel::onIntent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroceryListScreen(
    modifier: Modifier = Modifier,
    groceryGroups: List<GroceryGroup>,
    searchQuery: String,
    toolbarState: ToolbarState,
    toolbarHeightRange: IntRange,
    screenState: GroceryListScreenState,
    nestedScrollConnection: NestedScrollConnection? = null,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    addGroceryBottomSheetState: AddGroceryBottomSheetState = rememberAddGroceryBottomSheetState(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scrimIsShown: Boolean = false,
    toolbarIsHidden: Boolean = false,
    onIntent: (GroceryListScreenIntent) -> Unit = {},
    showEditGroceryBottomSheet: (String) -> Unit = {},
) {
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    BottomSheetScaffold(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
            .consumeWindowInsets(WindowInsets.navigationBars),
        sheetContent = {
            BackHandler(
                enabled = addGroceryBottomSheetState.handleBackButtonPress,
                onBack = { addGroceryBottomSheetState.onSheetDismissed() }
            )
            AddGroceryBottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp * 0.75F.dp)
                    .navigationBarsPadding(),
                searchQuery = searchQuery,
                contentType = screenState.bottomSheetContentType,
                clearSearchQueryButtonIsShown = screenState.clearSearchQueryButtonIsShown,
                customProduct = screenState.customProduct,
                grocerySearchResults = screenState.grocerySearchResults,
                previousGrocery = screenState.previousGrocery,
                showExtendedContent = addGroceryBottomSheetState.showExtendedContent,
                useExpandedPlaceholderText = addGroceryBottomSheetState.useExpandedPlaceholderText,
                changeSearchFieldFocusEvent = addGroceryBottomSheetState.changeSearchFieldFocusEvent,
                showCancelButtonInsteadOfFab = addGroceryBottomSheetState.showCancelButtonInsteadOfFab,
                onKeyboardDone = {
                    addGroceryBottomSheetState.onKeyboardDone()
                    onIntent(GroceryListScreenIntent.OnSearchFieldKeyboardDone)
                },
                onCancelButtonClicked = addGroceryBottomSheetState::onCancelButtonClicked,
                onFabClicked = addGroceryBottomSheetState::onFabClicked,
                onSearchFieldFocusChanged = addGroceryBottomSheetState::onSearchFieldFocusChanged,
                onSearchQueryChanged = { onIntent(GroceryListScreenIntent.OnSearchQueryChanged(it)) },
                onClearSearchQuery = { onIntent(GroceryListScreenIntent.OnClearSearchQuery) },
                onGrocerySearchResultClick = {
                    onIntent(
                        GroceryListScreenIntent.OnGrocerySearchResultClick(it)
                    )
                },
                onCustomProductClick = { onIntent(GroceryListScreenIntent.OnCustomProductClick) },
                onEditGroceryClicked = {
                    screenState.previousGrocery?.let {
                        showEditGroceryBottomSheet(it.productId)
                    }
                },
            )
        },
        sheetDragHandle = { BottomSheetDragHandle() },
        sheetPeekHeight = 100.dp + navigationBarHeight,
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .then(
                    if (nestedScrollConnection != null) {
                        Modifier.nestedScroll(nestedScrollConnection)
                    } else Modifier
                )
        ) {
            GroceryListCollapsingToolbar(
                listName = screenState.listName,
                toolbarHeightRange = toolbarHeightRange,
                toolbarState = toolbarState,
                toolbarIsHidden = toolbarIsHidden,
            )

            GroceryGrid(
                modifier = Modifier.fillMaxSize(),
                groceryGroups = groceryGroups,
                onGroceryClick = { onIntent(GroceryListScreenIntent.OnGroceryItemClick(it)) },
                onGroceryLongClick = {
                    showEditGroceryBottomSheet(it.productId)
                },
                lazyGridState = lazyGridState,
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        if (scrimIsShown) {
            Scrim(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { addGroceryBottomSheetState.onSheetDismissed() },
                )
            )
        }
    }
}

@Composable
private fun GroceryListCollapsingToolbar(
    modifier: Modifier = Modifier,
    listName: String,
    toolbarIsHidden: Boolean,
    toolbarHeightRange: IntRange,
    toolbarState: ToolbarState,
) {
    Column(modifier = modifier) {
        val toolbarEnterTransition = remember {
            expandVertically(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = LinearEasing,
                )
            )
        }
        val toolbarExitTransition = remember {
            shrinkVertically(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = LinearEasing,
                )
            )
        }

        AnimatedVisibility(
            visible = toolbarIsHidden,
            enter = toolbarEnterTransition,
            exit = toolbarExitTransition,
        ) {
            Spacer(
                modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
            )
        }

        AnimatedVisibility(
            visible = !toolbarIsHidden,
            enter = toolbarEnterTransition,
            exit = toolbarExitTransition,
        ) {
            val expandedTitleStyle = MaterialTheme.typography.headlineSmall
            CollapsingToolbar(
                modifier = Modifier.graphicsLayer {
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
                toolbarHeightRange = toolbarHeightRange,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroceryGrid(
    modifier: Modifier = Modifier,
    groceryGroups: List<GroceryGroup>,
    lazyGridState: LazyGridState,
    onGroceryClick: (Grocery) -> Unit,
    onGroceryLongClick: (Grocery) -> Unit,
) {
    val context = LocalContext.current
    GroupedLazyGroceryGrid(
        modifier = modifier,
        groceryGroups = groceryGroups,
        groceryItem = { grocery ->
            LazyGroceryGridItem(
                modifier = Modifier
                    .fillMaxSize()
                    .combinedClickable(
                        onClick = { onGroceryClick(grocery) },
                        onLongClick = { onGroceryLongClick(grocery) },
                    ),
                groceryName = grocery.name,
                groceryDescription = grocery.description,
                color = if (grocery.purchased) {
                    MaterialTheme.colorScheme.groceryListItemColors.purchasedBackgroundColor
                } else {
                    MaterialTheme.colorScheme.groceryListItemColors.defaultBackgroundColor
                },
                iconFile = remember(grocery.icon?.filePath) {
                    grocery.icon?.filePath?.let { filePath ->
                        File(context.filesDir, filePath)
                    }
                }
            )
        },
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
        lazyGridState = lazyGridState,
    )
}

val sampleGroceryGroups = listOf(
    GroceryGroup(
        titleId = null,
        groceries = List(5) {
            Grocery(
                name = "Not purchased grocery $it",
                purchased = false,
                productId = "not_purchased_$it"
            )
        }
    ),
    GroceryGroup(
        titleId = R.string.purchased_groceries_group_title,
        groceries = List(10) {
            Grocery(
                name = "Purchased grocery $it",
                purchased = true,
                productId = "purchased_$it"
            )
        }
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun GroceryListScreenPreview() {
    GroceryGeniusTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val collapsedToolbarHeight = 64.dp
            val expandedToolbarHeight = 112.dp
            val toolbarHeightRange = with(LocalDensity.current) {
                collapsedToolbarHeight.roundToPx()..expandedToolbarHeight.roundToPx()
            }
            val toolbarState = rememberExitUntilCollapsedToolbarState(toolbarHeightRange)
            GroceryListScreen(
                groceryGroups = sampleGroceryGroups,
                searchQuery = "",
                toolbarState = toolbarState,
                toolbarHeightRange = toolbarHeightRange,
                screenState = GroceryListScreenState(listName = "My Grocery List"),
            )
        }
    }
}
