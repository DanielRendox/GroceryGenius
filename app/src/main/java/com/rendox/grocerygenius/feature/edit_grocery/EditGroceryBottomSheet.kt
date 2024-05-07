package com.rendox.grocerygenius.feature.edit_grocery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.feature.edit_grocery.dialogs.CategoryPickerDialog
import com.rendox.grocerygenius.ui.components.BottomSheetDragHandle
import com.rendox.grocerygenius.ui.components.DeleteConfirmationDialog
import com.rendox.grocerygenius.ui.theme.GroceryGeniusTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroceryBottomSheet(
    modifier: Modifier = Modifier,
    editBottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    screenState: EditGroceryUiState,
    editGroceryDescription: TextFieldValue,
    hideBottomSheetOnCompletion: () -> Unit,
    onIntent: (EditGroceryUiIntent) -> Unit,
    navigateToIconPicker: (String) -> Unit,
) = Box(modifier = modifier) {
    var categoryPickerIsVisible by remember { mutableStateOf(false) }
    var deleteProductDialogIsVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val hideBottomSheet = {
        coroutineScope
            .launch { editBottomSheetState.hide() }
            .invokeOnCompletion { hideBottomSheetOnCompletion() }
    }
    ModalBottomSheet(
        modifier = Modifier.padding(top = 30.dp),
        onDismissRequest = { hideBottomSheet() },
        sheetState = editBottomSheetState,
        scrimColor = Color.Transparent,
        dragHandle = { BottomSheetDragHandle() },
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
                onIntent(EditGroceryUiIntent.OnDescriptionChanged(it))
            },
            onClearGroceryDescription = {
                onIntent(EditGroceryUiIntent.OnClearDescription)
            },
            onDoneButtonClick = {
                hideBottomSheet()
                focusManager.clearFocus()
            },
            onKeyboardDone = { hideBottomSheet() },
            itemDescriptionFocusRequester = itemDescriptionFocusRequester,
            onChangeCategoryClick = {
                categoryPickerIsVisible = true
            },
            onChangeIconClick = {
                screenState.editGrocery?.productId?.let { productId ->
                    coroutineScope
                        .launch {
                            editBottomSheetState.hide()
                            navigateToIconPicker(productId)
                            hideBottomSheetOnCompletion()
                        }
                }
            },
            productCanBeModified = screenState.editGrocery?.productIsDefault == false,
            onRemoveGrocery = {
                onIntent(EditGroceryUiIntent.OnRemoveGroceryFromList)
                hideBottomSheet()
            },
            onDeleteProduct = {
                deleteProductDialogIsVisible = true
            },
        )
    }

    if (categoryPickerIsVisible) {
        CategoryPickerDialog(
            modifier = Modifier,
            selectedCategoryId = screenState.editGrocery?.category?.id,
            categories = screenState.groceryCategories,
            onCategorySelected = {
                onIntent(EditGroceryUiIntent.OnCategorySelected(it))
                categoryPickerIsVisible = false
            },
            onDismissRequest = { categoryPickerIsVisible = false },
            onCustomCategorySelected = {
                onIntent(EditGroceryUiIntent.OnCustomCategorySelected)
                categoryPickerIsVisible = false
            },
        )
    }

    if (deleteProductDialogIsVisible) {
        DeleteConfirmationDialog(
            onConfirm = {
                screenState.editGrocery?.productId?.let {
                    onIntent(EditGroceryUiIntent.OnDeleteProduct)
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
                groceryDescription = TextFieldValue("Green, 32 bags"),
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