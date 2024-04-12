package com.rendox.grocerygenius.screens.grocery_lists_dashboard


import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@Composable
fun GroceryListDashboardRoute(
    groceryListsDashboardViewModel: GroceryListsDashboardViewModel = viewModel(),
) {
    val groceryLists by groceryListsDashboardViewModel.dashboardItemsFlow.collectAsStateWithLifecycle()

    GroceryListsDashboardScreen(
        groceryLists = groceryLists,
        updateLists = groceryListsDashboardViewModel::updateLists,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListsDashboardScreen(
    modifier: Modifier = Modifier,
    groceryLists: List<GroceryListsDashboardItem>,
    updateLists: (List<GroceryListsDashboardItem>) -> Unit = { },
    onFabClick: () -> Unit = { },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        }
    ) { paddingValues ->
        AndroidView(
            modifier = Modifier
                .padding(paddingValues)
                .padding(bottom = 12.dp),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                    layoutManager = LinearLayoutManager(context)
                    val adapter = GroceryListsDashboardAdapter(
                        recyclerView = this,
                        groceryLists = groceryLists,
                        updateLists = updateLists,
                    )
                    this.adapter = adapter
                }
            },
            update = { recyclerView ->
                val adapter = recyclerView.adapter as GroceryListsDashboardAdapter
                adapter.updateGroceryLists(groceryLists)
            }
        )
    }
}

@Preview
@Composable
private fun GroceryListsDashboardPreview() {
    GroceryGeniusTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            GroceryListsDashboardScreen(
                groceryLists = sampleDashboard,
            )
        }
    }
}

val sampleDashboard = listOf(
    GroceryListsDashboardItem(
        id = "1",
        name = "Sample List",
        itemCount = 5,
        items = emptyList()
    ),
    GroceryListsDashboardItem(
        id = "2",
        name = "Sample List 2",
        itemCount = 3,
        items = emptyList()
    ),
    GroceryListsDashboardItem(
        id = "3",
        name = "Sample List 3",
        itemCount = 7,
        items = emptyList()
    ),
    GroceryListsDashboardItem(
        id = "4",
        name = "Sample List 4",
        itemCount = 7,
        items = emptyList()
    ),
    GroceryListsDashboardItem(
        id = "5",
        name = "Sample List 5",
        itemCount = 11,
        items = emptyList()
    ),
    GroceryListsDashboardItem(
        id = "6",
        name = "Sample List 6",
        itemCount = 9,
        items = emptyList()
    ),
)
