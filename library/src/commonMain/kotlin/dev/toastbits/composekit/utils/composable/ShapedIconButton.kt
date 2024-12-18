@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import dev.toastbits.composekit.utils.common.thenIf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShapedIconButton(
    onClick: () -> Unit,
    colours: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    enabled: Boolean = true,
    applyWidth: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = ripple(
        bounded = false,
        radius = IconButtonDefaults.smallContainerSize().height / 2
    ),
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(IconButtonDefaults.smallContainerSize().height)
            .thenIf(applyWidth) {
                width(IconButtonDefaults.smallContainerSize().width)
            }
            .background(color = colours.containerColor(enabled), shape = shape)
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
        val content_colour: Color = colours.contentColor(enabled)
        CompositionLocalProvider(LocalContentColor provides content_colour, content = content)
    }
}
