package com.rendox.grocerygenius.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.rendox.grocerygenius.ui.theme.trainOneFontFamily
import java.io.File

@Composable
fun GroceryIcon(
    modifier: Modifier = Modifier,
    groceryName: String,
    iconFile: File?,
) {
    if (iconFile != null) {
        SubcomposeAsyncImage(
            modifier = modifier,
            model = iconFile,
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = LocalContentColor.current),
        ) {
            if (painter.state is AsyncImagePainter.State.Error) {
                TextIcon(title = groceryName)
            } else {
                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxSize()
                )
            }
        }
    } else {
        TextIcon(title = groceryName)
    }
}

@Composable
private fun TextIcon(
    modifier: Modifier = Modifier,
    title: String,
) {
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        val groceryNameFirstLetter = title.firstOrNull()?.uppercaseChar()
        val boxWithConstraintsScope = this
        val density = LocalDensity.current
        Text(
            modifier = modifier
                .fillMaxHeight()
                .offset(y = (-4).dp),
            text = groceryNameFirstLetter?.toString() ?: "",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = trainOneFontFamily,
            fontSize = with(density) { boxWithConstraintsScope.maxHeight.toSp() * 0.8F },
        )
    }
}