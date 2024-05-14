package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rendox.grocerygenius.ui.components.GroceryIcon
import com.rendox.grocerygenius.ui.theme.GroceryItemRounding
import java.io.File

@Composable
fun GroceryGridItem(
    modifier: Modifier = Modifier,
    groceryName: String,
    groceryDescription: String?,
    iconFile: File?,
    color: Color,
) {
    Surface(
        modifier = modifier.testTag("GroceryGridItem"),
        color = color,
    ) {
        BoxWithConstraints(modifier = Modifier.padding(8.dp)) {
            val boxWithConstraintsScope = this
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val textMeasurer = rememberTextMeasurer()
                val titleStyle = MaterialTheme.typography.labelMedium
                val descriptionStyle = MaterialTheme.typography.bodySmall
                val titleMaxLines = if (groceryDescription != null) 2 else 3
                val titleLayoutResult = remember(groceryName, boxWithConstraintsScope.constraints) {
                    textMeasurer.measure(
                        text = groceryName,
                        style = titleStyle,
                        constraints = boxWithConstraintsScope.constraints,
                        maxLines = titleMaxLines,
                    )
                }
                val descriptionMaxLines = if (titleLayoutResult.lineCount == 1) 2 else 1
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    GroceryIcon(
                        groceryName = groceryName,
                        iconFile = iconFile,
                    )
                }
                Box(
                    modifier = Modifier.heightIn(min = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val titleFontSize =
                            if (titleLayoutResult.hasVisualOverflow) 12.sp else 14.sp
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = groceryName,
                            style = titleStyle.copy(fontSize = titleFontSize),
                            lineHeight = titleFontSize,
                            textAlign = TextAlign.Center,
                            maxLines = titleMaxLines,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (groceryDescription != null) {
                            Text(
                                modifier = Modifier.padding(top = 2.dp),
                                text = groceryDescription,
                                style = descriptionStyle,
                                lineHeight = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = descriptionMaxLines,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Modifier.groceryGridItemCornerRounding(
    itemIndex: Int,
    numOfColumns: Int,
    lastIndex: Int,
) = this.clip(
    shape = RoundedCornerShape(
        topStart = when (itemIndex) {
            0 -> GroceryItemRounding
            else -> 0.dp
        },
        topEnd = when {
            itemIndex == (numOfColumns - 1) -> GroceryItemRounding
            (lastIndex + 1) < numOfColumns && itemIndex == lastIndex -> GroceryItemRounding
            else -> 0.dp
        },
        bottomStart = when (itemIndex) {
            lastIndex - (lastIndex % numOfColumns) -> GroceryItemRounding
            else -> 0.dp
        },
        bottomEnd = when  {
            itemIndex == lastIndex -> GroceryItemRounding
            itemIndex == lastIndex - (lastIndex % numOfColumns) - 1 &&
                    (lastIndex + 1) % numOfColumns != 0 -> GroceryItemRounding
            else -> 0.dp
        },
    )
)

class GridItemPreviewParameterProvider : PreviewParameterProvider<Pair<String, String?>> {
    override val values: Sequence<Pair<String, String?>>
        get() = sequenceOf(
            "Mozzarella" to null,
            "Dishwashing" to "Lemon Scent",
            "Echo Glow Smart Lamp" to "for kids room",
        )
}

@Preview
@Composable
private fun LazyGroceryGridItemPreview(
    @PreviewParameter(GridItemPreviewParameterProvider::class) titleAndDescription: Pair<String, String>,
) {
    GroceryGridItem(
        modifier = Modifier.size(104.dp),
        groceryName = titleAndDescription.first,
        groceryDescription = titleAndDescription.second,
        color = MaterialTheme.colorScheme.groceryListItemColors.defaultBackgroundColor,
        iconFile = null,
    )
}