package com.rendox.grocerygenius.ui.components.grocery_list

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rendox.grocerygenius.ui.theme.trainOneFontFamily

@Composable
fun LazyGroceryGridItem(
    modifier: Modifier = Modifier,
    groceryName: String,
    groceryDescription: String?,
    groceryIcon: Bitmap?,
    color: Color,
) {
    Surface(
        modifier = modifier,
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
                val descriptionLayoutResult = remember(
                    groceryDescription,
                    descriptionMaxLines,
                    boxWithConstraintsScope.constraints
                ) {
                    groceryDescription?.let {
                        textMeasurer.measure(
                            text = it,
                            style = descriptionStyle,
                            constraints = boxWithConstraintsScope.constraints,
                            maxLines = descriptionMaxLines,
                        )
                    }
                }
                val totalLinesOfText =
                    titleLayoutResult.lineCount + (descriptionLayoutResult?.lineCount ?: 0)
                val textShouldBeSmaller = totalLinesOfText > 2
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    GroceryIcon(
                        textShouldBeSmaller = textShouldBeSmaller,
                        groceryName = groceryName,
                        bitmap = groceryIcon,
                    )
                }
                Box(
                    modifier = Modifier.heightIn(min = 44.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val titleFontSize = if (titleLayoutResult.hasVisualOverflow) 12.sp else 14.sp
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

@Composable
private fun GroceryIcon(
    modifier: Modifier = Modifier,
    groceryName: String,
    textShouldBeSmaller: Boolean,
    bitmap: Bitmap?,
) {
    if (bitmap != null) {
        Icon(
            modifier = modifier.fillMaxSize(),
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
        )
    } else {
        val groceryNameFirstLetter = groceryName.firstOrNull()?.uppercaseChar()
        Text(
            modifier = modifier.fillMaxHeight(),
            text = groceryNameFirstLetter?.toString() ?: "",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = trainOneFontFamily,
            fontSize = if (textShouldBeSmaller) 40.sp else 48.sp,
        )
    }
}

class NameParameterProvider : PreviewParameterProvider<Pair<String, String?>> {
    override val values: Sequence<Pair<String, String?>>
        get() = sequenceOf(
            "Mocarella" to null,
            "Dishwashing" to "Lemon Scent",
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
        groceryIcon = null,
    )
}