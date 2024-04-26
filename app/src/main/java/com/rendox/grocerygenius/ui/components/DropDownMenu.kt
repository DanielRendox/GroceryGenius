package com.rendox.grocerygenius.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.ui.components.scrollbar.DecorativeScrollbar
import com.rendox.grocerygenius.ui.components.scrollbar.scrollbarState

@Composable
fun LazyDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<String>,
    onOptionSelected: (Int) -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        val lazyListState = rememberLazyListState()
        val scrollbarState = lazyListState.scrollbarState(itemsAvailable = options.size)
        Box(
            modifier = Modifier
                .width(125.dp)
                .height(200.dp)
        ) {
            LazyColumn(state = lazyListState) {
                items(options) { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            onOptionSelected(options.indexOf(option))
                            onDismissRequest()
                        }
                    )
                }
            }

            lazyListState.DecorativeScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterEnd),
                state = scrollbarState,
                orientation = Orientation.Vertical,
            )
        }
    }
}

@Composable
fun DropDownMenuToggleIcon(expanded: Boolean) {
    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "animateFloatAsState",
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )
    Icon(
        modifier = Modifier
            .padding(start = 8.dp)
            .graphicsLayer { rotationZ = iconRotation },
        imageVector = Icons.Default.ArrowDropDown,
        contentDescription = stringResource(R.string.toggle_dropdown_menu_visibility),
    )
}