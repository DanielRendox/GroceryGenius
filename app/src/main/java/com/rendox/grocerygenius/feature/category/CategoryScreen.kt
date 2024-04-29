package com.rendox.grocerygenius.feature.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbar
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.CollapsingToolbarScaffoldScrollableState
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.CollapsingToolbarNestedScrollConnection
import com.rendox.grocerygenius.ui.components.collapsing_toolbar.scroll_behavior.rememberExitUntilCollapsedToolbarState
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGrid
import com.rendox.grocerygenius.ui.components.grocery_list.LazyGroceryGridItem
import com.rendox.grocerygenius.ui.components.grocery_list.groceryListItemColors
import com.rendox.grocerygenius.ui.theme.TopAppBarMediumHeight
import com.rendox.grocerygenius.ui.theme.TopAppBarSmallHeight
import java.io.File

@Composable
fun CategoryRoute(
    viewModel: CategoryViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val categoryName by viewModel.categoryNameFlow.collectAsStateWithLifecycle()
    val groceries by viewModel.groceriesFlow.collectAsStateWithLifecycle()
    CategoryScreen(
        categoryName = categoryName,
        groceries = groceries,
        onGroceryClick = viewModel::onGroceryClick,
        navigateBack = navigateBack,
    )
}

@Composable
fun CategoryScreen(
    categoryName: String,
    groceries: List<Grocery>,
    onGroceryClick: (Grocery) -> Unit,
    navigateBack: () -> Unit,
) {
    val toolbarHeightRange = with(LocalDensity.current) {
        TopAppBarSmallHeight.roundToPx()..TopAppBarMediumHeight.roundToPx()
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

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .nestedScroll(nestedScrollConnection)
    ) {
        val titleStyle = MaterialTheme.typography.headlineSmall
        CollapsingToolbar(
            toolbarState = toolbarState,
            toolbarHeightRange = toolbarHeightRange,
            titleExpanded = {
                Text(
                    text = categoryName,
                    style = titleStyle.copy(textMotion = TextMotion.Animated),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            expandedTitleFontSize = titleStyle.fontSize,
            titleBottomPadding = 24.dp,
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            },
        )
        LazyGroceryGrid(
            groceries = groceries,
            lazyGridState = lazyGridState,
            groceryItem = { grocery ->
                LazyGroceryGridItem(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onGroceryClick(grocery) },
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
        )
    }
}