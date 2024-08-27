package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.Indication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

actual fun Modifier.platformClickable(
    enabled: Boolean,
    onClick: (() -> Unit)?,
    onAltClick: (() -> Unit)?,
    onAlt2Click: (() -> Unit)?,
    indication: Indication?
): Modifier =
    composed {
        combinedClickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = indication,
            onClick = { onClick?.invoke() },
            onLongClick = { onAltClick?.invoke() },
            onDoubleClick = { onAlt2Click?.invoke() }
        )
    }

actual fun Modifier.platformClickableWithOffset(
    enabled: Boolean,
    onClick: ((Offset) -> Unit)?,
    onAltClick: ((Offset) -> Unit)?,
    onAlt2Click: ((Offset) -> Unit)?,
    indication: Indication?
): Modifier =
    composed {
        pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick?.invoke(it) },
                onLongPress = onAltClick,
                onDoubleTap = onAlt2Click
            )
        }
    }
