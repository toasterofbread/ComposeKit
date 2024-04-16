package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.Indication
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.isPrimary
import androidx.compose.ui.input.pointer.isSecondary
import androidx.compose.ui.input.pointer.isTertiary
import androidx.compose.ui.input.pointer.PointerButton
import dev.toastbits.composekit.utils.common.thenWith
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.onClick as composeOnClick
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

actual fun Modifier.platformClickable(
    enabled: Boolean,
    onClick: (() -> Unit)?,
    onAltClick: (() -> Unit)?,
    onAlt2Click: (() -> Unit)?,
    indication: Indication?
): Modifier =
    if (!enabled) this
    else this
        .thenWith(onClick) {
            composeOnClick(
                matcher = PointerMatcher.mouse(PointerButton.Primary),
                onClick = it
            )
        }
        .thenWith(onAltClick) {
            composeOnClick(
                matcher = PointerMatcher.mouse(PointerButton.Secondary),
                onClick = it
            )
        }
        .thenWith(onAlt2Click) {
            composeOnClick(
                matcher = PointerMatcher.mouse(PointerButton.Tertiary),
                onClick = it
            )
        }

actual fun Modifier.platformClickableWithOffset(
    enabled: Boolean,
    onClick: ((Offset) -> Unit)?,
    onAltClick: ((Offset) -> Unit)?,
    onAlt2Click: ((Offset) -> Unit)?,
    indication: Indication?
): Modifier = 
    if (!enabled) this
    else this
        .thenWith(onClick) {
            detectReleaseEvents(it) { event ->
                if (event.button.isPrimary) {
                    it(event.changes.first().position)
                }
            }
        }
        .thenWith(onAltClick) {
            detectReleaseEvents(it) { event ->
                if (event.button.isSecondary) {
                    it(event.changes.first().position)
                }
            }
        }
        .thenWith(onAlt2Click) {
            detectReleaseEvents(it) { event ->
                if (event.button.isTertiary) {
                    it(event.changes.first().position)
                }
            }
        }

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
