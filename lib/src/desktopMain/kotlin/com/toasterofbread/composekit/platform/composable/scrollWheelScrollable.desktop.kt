package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.animation.core.tween
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*

private const val SCROLL_DISTANCE_MULTIPLER: Float = 100f

actual fun Modifier.scrollWheelScrollable(
    state: ScrollableState,
    reverse_direction: Boolean
): Modifier = composed {
    val coroutine_scope: CoroutineScope = rememberCoroutineScope()

    onPointerEvent(PointerEventType.Scroll) {
        val distance: Float = it.changes.first().scrollDelta.y * SCROLL_DISTANCE_MULTIPLER * (if (reverse_direction) -1 else 1)

        coroutine_scope.launch {
            if (state.isScrollInProgress) {
                state.animateScrollBy(distance * 1.5f, tween(durationMillis = 150, easing = LinearOutSlowInEasing))
            }
            else {
                state.animateScrollBy(distance, tween(durationMillis = 200, easing = LinearOutSlowInEasing))
            }
        }
    }
}
