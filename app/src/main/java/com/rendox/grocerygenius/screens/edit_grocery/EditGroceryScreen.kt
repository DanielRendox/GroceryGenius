package com.rendox.grocerygenius.screens.edit_grocery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.screens.edit_grocery.dialogs.CategoryPickerDialog
import com.rendox.grocerygenius.screens.edit_grocery.dialogs.IconPickerDialog
import com.rendox.grocerygenius.screens.edit_grocery.dialogs.PickerDialogType
import com.rendox.grocerygenius.ui.components.BottomSheetDragHandle
import com.rendox.grocerygenius.ui.components.DeleteConfirmationDialog
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroceryScreen(
    modifier: Modifier = Modifier,
    editBottomSheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    screenState: EditGroceryScreenState,
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
            groceryName = screenState.editGrocery?.name ?: "",
            groceryDescription = editGroceryDescription,
            clearGroceryDescriptionButtonIsShown = screenState.clearEditGroceryDescriptionButtonIsShown,
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
            productCanBeModified = screenState.editGrocery?.productIsDefault == false,
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
                selectedCategoryId = screenState.editGrocery?.category?.id,
                categories = screenState.groceryCategories,
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
            val context = LocalContext.current
            IconPickerDialog(
                modifier = Modifier,
                numOfIcons = screenState.icons.size,
                icon = { index ->
                    AsyncImage(
                        modifier = modifier.fillMaxSize(),
                        model = File(context.filesDir, screenState.icons[index].filePath),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = LocalContentColor.current)
                    )
                },
                title = { screenState.icons[it].name },
                onIconSelected = {
                    onIntent(EditGroceryScreenIntent.OnIconSelected(screenState.icons[it].id))
                    pickerDialog = PickerDialogType.None
                },
                onDismissRequest = { pickerDialog = PickerDialogType.None },
            )
        }
    }

    if (deleteProductDialogIsVisible) {
        DeleteConfirmationDialog(
            onConfirm = {
                screenState.editGrocery?.productId?.let {
                    onIntent(EditGroceryScreenIntent.OnDeleteProduct)
                }
                hideBottomSheet()
                deleteProductDialogIsVisible = false
            },
            onDismissRequest = { deleteProductDialogIsVisible = false },
            bodyText = stringResource(R.string.delete_product_dialog_text),
        )
    }
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