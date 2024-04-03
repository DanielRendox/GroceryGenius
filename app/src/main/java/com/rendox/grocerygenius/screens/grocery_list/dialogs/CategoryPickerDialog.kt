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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@Composable
fun CategoryPickerDialog(
    modifier: Modifier = Modifier,
    selectedCategoryId: String?,
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    onCustomCategorySelected: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    PickerDialog(
        modifier = modifier,
        title = stringResource(R.string.select_category_dialog_title),
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(modifier = Modifier.weight(1F)) {
            item(key = categories.size) {
                CategoryOption(
                    isSelected = selectedCategoryId == null,
                    categoryName = stringResource(R.string.custom_category_title),
                    onClick = onCustomCategorySelected,
                )
            }
            items(
                items = categories,
                key = { it.id },
            ) { category ->
                CategoryOption(
                    onClick = { onCategorySelected(category) },
                    isSelected = category.id == selectedCategoryId,
                    categoryName = category.name,
                )
            }
        }
    }
}

@Composable
private fun CategoryOption(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    categoryName: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            modifier = Modifier.padding(start = 16.dp),
            selected = isSelected,
            onClick = onClick,
        )
        Text(text = categoryName)
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
                onCustomCategorySelected = {},
            )
        }
    }
}