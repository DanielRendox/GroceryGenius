package com.rendox.grocerygenius.screens.edit_grocery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.screens.edit_grocery.dialogs.PickerDialogType
import com.rendox.grocerygenius.screens.edit_grocery.dialogs.CategoryPickerDialog
import com.rendox.grocerygenius.screens.edit_grocery.dialogs.IconPickerDialog
import com.rendox.grocerygenius.ui.components.BottomSheetDragHandle
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroceryScreen(
    modifier: Modifier = Modifier,
    editBottomSheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    uiState: EditGroceryScreenState,
    editGroceryDescription: String?,
    hideBottomSheet: () -> Unit,
    onIntent: (EditGroceryScreenIntent) -> Unit,
) = Box(modifier = modifier) {
    var pickerDialog by remember { mutableStateOf(PickerDialogType.None) }
    var deleteProductDialogIsVisible by remember { mutableStateOf(false) }

    ModalBottomSheet(
        modifier = Modifier.padding(top = 30.dp),
        onDismissRequest = hideBottomSheet,
        sheetState = editBottomSheetState,
        scrimColor = Color.Transparent,
        dragHandle = { BottomSheetDragHandle() }
    ) {
        val itemDescriptionFocusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        LaunchedEffect(editBottomSheetState.currentValue) {
            if (editBottomSheetState.currentValue == SheetValue.Expanded) {
                itemDescriptionFocusRequester.requestFocus()
            }
        }
        EditGroceryBottomSheetContent(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            groceryName = uiState.editGrocery?.name ?: "",
            groceryDescription = editGroceryDescription,
            clearGroceryDescriptionButtonIsShown = uiState.clearEditGroceryDescriptionButtonIsShown,
            onGroceryDescriptionChanged = {
                onIntent(EditGroceryScreenIntent.OnDescriptionChanged(it))
            },
            onClearGroceryDescription = {
                onIntent(EditGroceryScreenIntent.OnClearDescription)
            },
            onDoneButtonClick = {
                hideBottomSheet()
                focusManager.clearFocus()
            },
            onKeyboardDone = hideBottomSheet,
            itemDescriptionFocusRequester = itemDescriptionFocusRequester,
            onChangeCategoryClick = {
                pickerDialog = PickerDialogType.CategoryPicker
            },
            onChangeIconClick = {
                pickerDialog = PickerDialogType.IconPicker
            },
            productCanBeModified = uiState.editGrocery?.productIsDefault == false,
            onRemoveGrocery = {
                onIntent(EditGroceryScreenIntent.OnRemoveGroceryFromList)
                hideBottomSheet()
            },
            onDeleteProduct = {
                deleteProductDialogIsVisible = true
            },
        )
    }

    when (pickerDialog) {
        PickerDialogType.None -> {}
        PickerDialogType.CategoryPicker -> {
            CategoryPickerDialog(
                modifier = Modifier,
                selectedCategoryId = uiState.editGrocery?.category?.id,
                categories = uiState.groceryCategories,
                onCategorySelected = {
                    onIntent(EditGroceryScreenIntent.OnCategorySelected(it.id))
                    pickerDialog = PickerDialogType.None
                },
                onDismissRequest = { pickerDialog = PickerDialogType.None },
                onCustomCategorySelected = {
                    onIntent(EditGroceryScreenIntent.OnCustomCategorySelected)
                    pickerDialog = PickerDialogType.None
                },
            )
        }

        PickerDialogType.IconPicker -> {
            IconPickerDialog(
                modifier = Modifier,
                numOfIcons = uiState.icons.size,
                icon = {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.sample_grocery_icon),
                        contentDescription = null,
                    )
                },
                title = { uiState.icons[it].name },
                onIconSelected = {
                    onIntent(EditGroceryScreenIntent.OnIconSelected(uiState.icons[it].id))
                    pickerDialog = PickerDialogType.None
                },
                onDismissRequest = { pickerDialog = PickerDialogType.None },
            )
        }
    }

    if (deleteProductDialogIsVisible) {
        DeleteProductConfirmationDialog(
            onConfirm = {
                uiState.editGrocery?.productId?.let {
                    onIntent(EditGroceryScreenIntent.OnDeleteProduct)
                }
                hideBottomSheet()
                deleteProductDialogIsVisible = false
            },
            onDismissRequest = { deleteProductDialogIsVisible = false },
        )
    }
}

@Composable
fun DeleteProductConfirmationDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "${stringResource(R.string.delete)}?")
        },
        text = {
            Text(text = stringResource(R.string.delete_product_dialog_text))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
    )
}

class ParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@Preview
@Composable
private fun EditGroceryBottomSheetContentPreview(
    @PreviewParameter(ParameterProvider::class) productCanBeModified: Boolean,
) {
    GroceryGeniusTheme {
        Surface {
            EditGroceryBottomSheetContent(
                modifier = Modifier.padding(16.dp),
                groceryName = "Tea",
                groceryDescription = "Green, 32 bags",
                clearGroceryDescriptionButtonIsShown = false,
                onGroceryDescriptionChanged = {},
                onClearGroceryDescription = {},
                onKeyboardDone = {},
                onDoneButtonClick = {},
                itemDescriptionFocusRequester = remember { FocusRequester() },
                onChangeCategoryClick = {},
                onChangeIconClick = {},
                productCanBeModified = productCanBeModified,
                onRemoveGrocery = {},
                onDeleteProduct = {},
            )
        }
    }
}