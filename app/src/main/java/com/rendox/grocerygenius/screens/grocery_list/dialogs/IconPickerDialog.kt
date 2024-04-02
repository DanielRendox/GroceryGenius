package com.rendox.grocerygenius.screens.grocery_list.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
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
        LazyVerticalGrid(
            modifier = modifier.weight(1F).padding(16.dp),
            columns = GridCells.Adaptive(64.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                count = numOfIcons,
                key = { it },
            ) { index ->
                Surface(
                    onClick = { onIconSelected(index) },
                    modifier = Modifier.aspectRatio(1F),
                    shape = CornerRoundingDefault,
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
                            modifier = Modifier.padding(vertical = 4.dp),
                            text = title(index),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
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
                        painter = painterResource(R.drawable.sample_grocery_icon),
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