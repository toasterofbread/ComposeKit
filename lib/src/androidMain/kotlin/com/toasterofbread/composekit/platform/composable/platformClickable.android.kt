package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.Indication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset

actual fun Modifier.platformClickable(
    enabled: Boolean,
    onClick: ((Offset) -> Unit)?,
    onAltClick: ((Offset) -> Unit)?,
    onAlt2Click: ((Offset) -> Unit)?,
    indication: Indication?
): Modifier =
    composed {
        combinedClickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = indication,
            // TODO
            onClick = { onClick?.invoke(Offset.Zero) },
            onLongClick = onAltClick?.let {{ it(Offset.Zero) }},
            onDoubleClick = onAlt2Click?.let {{ it(Offset.Zero) }}
        )
    }
