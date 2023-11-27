package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

expect fun Modifier.platformClickable(
    enabled: Boolean = true,
    onClick: ((Offset) -> Unit)? = null,
    onAltClick: ((Offset) -> Unit)? = null,
    onAlt2Click: ((Offset) -> Unit)? = null,
    indication: Indication? = null
): Modifier

internal fun Modifier.detectReleaseEvents(key: Any?, action: (PointerEvent) -> Unit): Modifier {
    return pointerInput(key) {
        while (currentCoroutineContext().isActive) {
            awaitPointerEventScope {
                val event: PointerEvent = awaitPointerEvent(PointerEventPass.Initial)
                if (event.type == PointerEventType.Release) {
                    action(event)
                }
            }
        }
    }
}