package com.rendox.grocerygenius.ui.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun TonalDataInput(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    indication: Indication? = LocalIndication.current,
    dropDownMenu: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(30))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(
                onClick = onClick,
                indication = indication,
                interactionSource = remember { MutableInteractionSource() },
            ),
    ) {
        content()
        dropDownMenu?.let { it() }
    }
}

