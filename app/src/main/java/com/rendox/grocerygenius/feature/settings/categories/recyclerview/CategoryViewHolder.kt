package com.rendox.grocerygenius.feature.settings.categories.recyclerview

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Category

class CategoryViewHolder(
    private val composeView: ComposeView,
    private val onDrag: (RecyclerView.ViewHolder) -> Unit,
) : RecyclerView.ViewHolder(composeView) {

    fun bind(category: Category) {
        composeView.setContent {
            CategoryItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                category = category,
                onDrag = { onDrag(this) },
            )
        }
    }
}

@Composable
private fun CategoryItem(
    modifier: Modifier = Modifier,
    category: Category,
    onDrag: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 12.dp,
                )
                .weight(1f),
            text = category.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .draggable(
                    state = rememberDraggableState { onDrag() },
                    startDragImmediately = true,
                    orientation = Orientation.Vertical,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(id = R.drawable.baseline_drag_handle_24),
                contentDescription = null,
            )
        }
    }
}