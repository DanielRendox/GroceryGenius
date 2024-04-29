package com.rendox.grocerygenius.feature.edit_grocery.dialogs

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.components.scrollbar.DraggableScrollbar
import com.rendox.grocerygenius.ui.components.scrollbar.rememberDraggableScroller
import com.rendox.grocerygenius.ui.components.scrollbar.scrollbarState
import com.rendox.grocerygenius.ui.theme.CornerRoundingDefault
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@Composable
fun IconPickerDialog(
    modifier: Modifier = Modifier,
    numOfIcons: Int,
    onIconSelected: (Int) -> Unit,
    icon: @Composable (Int) -> Unit,
    title: (Int) -> String,
    onDismissRequest: () -> Unit,
) {
    PickerDialog(
        title = stringResource(R.string.select_icon_dialog_title),
        onDismissRequest = onDismissRequest,
    ) {
        Box(modifier = modifier.weight(1F).padding(vertical = 16.dp)) {
            val lazyGridState = rememberLazyGridState()
            val scrollbarState = lazyGridState.scrollbarState(
                itemsAvailable = numOfIcons,
            )
            LazyVerticalGrid(
                modifier = Modifier.padding(horizontal = 16.dp),
                columns = GridCells.Adaptive(88.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyGridState,
            ) {
                items(
                    count = numOfIcons,
                    key = { it },
                ) { index ->
                    Surface(
                        onClick = { onIconSelected(index) },
                        modifier = Modifier.aspectRatio(1F),
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Box(modifier = Modifier.weight(1F)) {
                                icon(index)
                            }
                            Text(
                                modifier = Modifier.padding(vertical = 8.dp),
                                text = title(index),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
            lazyGridState.DraggableScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterEnd),
                state = scrollbarState,
                orientation = Orientation.Vertical,
                onThumbMoved = lazyGridState.rememberDraggableScroller(
                    itemsAvailable = numOfIcons,
                ),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun IconPickerDialogContentPreview() {
    GroceryGeniusTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            IconPickerDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                numOfIcons = 10,
                icon = {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                    )
                },
                title = { "Icon $it" },
                onIconSelected = {},
                onDismissRequest = {},
            )
        }
    }
}