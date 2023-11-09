@file:OptIn(ExperimentalMaterialApi::class)

package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.swipeable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
actual fun Modifier.scrollWheelSwipeable(
    state: SwipeableState<Int>,
    anchors: Map<Float, Int>,
    thresholds: (from: Int, to: Int) -> ThresholdConfig,
    orientation: Orientation,
    reverse_direction: Boolean,
    interaction_source: MutableInteractionSource?
): Modifier = composed {
    return@composed mouseWheelInput { direction ->
        val target = state.targetValue + (if (reverse_direction) -direction else direction)
        if (anchors.values.contains(target)) {
            try {
                state.animateTo(target)
            } catch (_: Throwable) {}
        }
        return@mouseWheelInput true
    }.swipeable(state = state, anchors = anchors, thresholds = thresholds, orientation = orientation, reverseDirection = reverse_direction, interactionSource = interaction_source)
}

internal inline val PointerEvent.isConsumed: Boolean get() = changes.any { c: PointerInputChange -> c.isConsumed }
internal inline fun PointerEvent.consume() = changes.forEach { c: PointerInputChange -> c.consume() }

internal suspend fun AwaitPointerEventScope.awaitScrollEvent(): PointerEvent {
    var event: PointerEvent
    do {
        event = awaitPointerEvent()
    } while (event.type != PointerEventType.Scroll)
    return event
}

internal fun Modifier.mouseWheelInput(
    onMouseWheel: suspend (direction: Int) -> Boolean
) = pointerInput(Unit) {
    coroutineScope {
        while (isActive) {
            val event = awaitPointerEventScope {
                awaitScrollEvent()
            }
            if (!event.isConsumed) {
                val change: PointerInputChange = event.changes.first()
                launch {
                    val consumed = onMouseWheel(change.scrollDelta.y.toInt())
                    if (consumed) {
                        event.consume()
                    }
                }
            }
        }
    }
}
