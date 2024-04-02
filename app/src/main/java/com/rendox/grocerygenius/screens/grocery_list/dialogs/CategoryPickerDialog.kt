package com.rendox.grocerygenius.screens.grocery_list.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@Composable
fun CategoryPickerDialog(
    modifier: Modifier = Modifier,
    selectedCategoryId: String,
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    onDismissRequest: () -> Unit,
) {
    PickerDialog(
        modifier = modifier,
        title = "Choose category",
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(modifier = Modifier.weight(1F)) {
            items(
                items = categories,
                key = { it.id },
            ) { category ->
                val interactionSource = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = { onCategorySelected(category) },
                            interactionSource = interactionSource,
                            indication = null,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        modifier = Modifier.padding(start = 16.dp),
                        selected = category.id == selectedCategoryId,
                        onClick = { onCategorySelected(category) },
                    )
                    Text(text = category.name)
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ChooseCategoryDialogPreview() {
    val categories = remember {
        listOf(
            Category("1", "Fruit"),
            Category("2", "Vegetable"),
            Category("3", "Meat"),
        )
    }
    GroceryGeniusTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CategoryPickerDialog(
                modifier = Modifier
                    .width(200.dp)
                    .height(400.dp),
                selectedCategoryId = "1",
                categories = categories,
                onCategorySelected = {},
                onDismissRequest = {},
            )
        }
    }
}