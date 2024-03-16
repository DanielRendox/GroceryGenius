package com.rendox.grocerygenius.ui.components.grocery_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rendox.grocerygenius.R
import com.rendox.grocerygenius.model.Grocery

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyGroceryGrid(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    groceryGroups: List<GroceryGroup>,
    groceryItem: @Composable (Grocery) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = lazyGridState,
        contentPadding = contentPadding,
        columns = GridCells.Adaptive(104.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Add dummy item to prevent automatic scroll when the first item is clicked
        // (this is a workaround for an internal bug in LazyVerticalGrid)

        item(
            key = -1,
            span = { GridItemSpan(maxLineSpan) },
            contentType = "Dummy",
        ) {
            Spacer(modifier = Modifier.height(0.dp))
        }

        groceryGroups.forEachIndexed { groupIndex, group ->
            if (group.titleId != null) {
                item(
                    // negative indices as positive ones are occupied by items
                    // subtract 1 as indices start from zero
                    // subtract 1 again as -1 is occupied by the dummy item
                    key = -groupIndex - 2,
                    span = { GridItemSpan(maxLineSpan) },
                    contentType = "Title"
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .animateItemPlacement(),
                        text = stringResource(id = R.string.not_purchased_groceries_group_title),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            items(
                items = group.groceries,
                key = { it.id },
                contentType = { "Grocery" }
            ) { grocery ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1F)
                        .animateItemPlacement()
                ) {
                    groceryItem(grocery)
                }
            }
        }
    }
}

@Composable
fun LazyGroceryGridItem(
    modifier: Modifier = Modifier,
    grocery: Grocery,
    notPurchasedColor: Color = MaterialTheme.colorScheme.primary,
    purchasedColor: Color = MaterialTheme.colorScheme.tertiary,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (grocery.purchased) purchasedColor else notPurchasedColor,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.weight(1F)) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                )
            }
            BoxWithConstraints(
                modifier = Modifier.heightIn(min = 44.dp),
                contentAlignment = Alignment.Center,
            ) {
                val boxWithConstraintsScope = this

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val textMeasurer = rememberTextMeasurer()
                    val titleStyle = MaterialTheme.typography.labelMedium
                    val titleMaxLines = if (grocery.description != null) 2 else 3
                    val textLayoutResult = remember(grocery.name) {
                        textMeasurer.measure(
                            text = grocery.name,
                            style = titleStyle,
                            constraints = boxWithConstraintsScope.constraints,
                            maxLines = titleMaxLines,
                        )
                    }

                    val titleFontSize = if (textLayoutResult.hasVisualOverflow) 12.sp else 14.sp
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = grocery.name,
                        style = titleStyle.copy(fontSize = titleFontSize),
                        lineHeight = titleFontSize,
                        textAlign = TextAlign.Center,
                        maxLines = titleMaxLines,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (grocery.description != null) {
                        Text(
                            modifier = Modifier.padding(top = 2.dp),
                            text = grocery.description,
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
        grocery = Grocery(
            id = 0,
            name = titleAndDescription.first,
            purchased = false,
            description = titleAndDescription.second,
        ),
        onClick = {},
    )
}