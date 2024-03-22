package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rendox.grocerygenius.R

@Composable
fun LazyGroceryGridItem(
    modifier: Modifier = Modifier,
    groceryName: String,
    groceryDescription: String?,
    color: Color,
    icon: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = color,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.weight(1F)) {
                icon()
            }
            BoxWithConstraints(
                modifier = Modifier.heightIn(min = 44.dp),
                contentAlignment = Alignment.Center,
            ) {
                val boxWithConstraintsScope = this

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val textMeasurer = rememberTextMeasurer()
                    val titleStyle = MaterialTheme.typography.labelMedium
                    val titleMaxLines = if (groceryDescription != null) 2 else 3
                    val textLayoutResult = remember(groceryName) {
                        textMeasurer.measure(
                            text = groceryName,
                            style = titleStyle,
                            constraints = boxWithConstraintsScope.constraints,
                            maxLines = titleMaxLines,
                        )
                    }

                    val titleFontSize = if (textLayoutResult.hasVisualOverflow) 12.sp else 14.sp
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
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 12.sp,
                            textAlign = TextAlign.Center,
                            maxLines = if (textLayoutResult.lineCount == 1) 2 else 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

class NameParameterProvider : PreviewParameterProvider<Pair<String, String>> {
    override val values: Sequence<Pair<String, String>>
        get() = sequenceOf(
            "Pasta" to "Gourmet Pasta Collection",
            "Dishwashing liquid" to "Fresh Lemon Scent",
            "Echo Glow Smart Lamp" to "for kids room",
        )
}

@Preview
@Composable
private fun LazyGroceryGridItemPreview(
    @PreviewParameter(NameParameterProvider::class) titleAndDescription: Pair<String, String>,
) {
    LazyGroceryGridItem(
        modifier = Modifier.size(104.dp),
        groceryName = titleAndDescription.first,
        groceryDescription = titleAndDescription.second,
        color = MaterialTheme.colorScheme.groceryListItemColors.defaultBackgroundColor,
        icon = {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.sample_grocery_icon),
                contentDescription = null,
            )
        }
    )
}