package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.model.Grocery

@Composable
fun GroceryList(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    groceries: List<GroceryGroup>,
    onGroceryItemClick: (Grocery) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyGroceryGrid(
        modifier = modifier,
        lazyGridState = lazyGridState,
        groceryGroups = groceries,
        onGroceryItemClick = onGroceryItemClick,
        contentPadding = contentPadding,
        columns = GridCells.Adaptive(104.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    )
}