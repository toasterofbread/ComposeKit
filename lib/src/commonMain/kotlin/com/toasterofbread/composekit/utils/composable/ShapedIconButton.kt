@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.toasterofbread.composekit.utils.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.tokens.IconButtonTokens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShapedIconButton(
    onClick: () -> Unit,
    colours: IconButtonColors,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = rememberRipple(
        bounded = false,
        radius = IconButtonTokens.StateLayerSize / 2
    ),
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(IconButtonTokens.StateLayerSize)
            .background(color = colours.containerColor(enabled).value, shape = shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = indication
            ),
        contentAlignment = Alignment.Center
    ) {
        val content_colour: Color = colours.contentColor(enabled).value
        CompositionLocalProvider(LocalContentColor provides content_colour, content = content)
    }
}
