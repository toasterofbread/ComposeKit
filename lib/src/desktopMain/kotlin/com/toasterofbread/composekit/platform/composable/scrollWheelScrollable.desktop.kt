package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

actual fun Modifier.scrollWheelScrollable(
    state: ScrollableState,
    reverse_direction: Boolean
): Modifier = composed {
    val coroutine_scope: CoroutineScope = rememberCoroutineScope()

    onPointerEvent(PointerEventType.Scroll) {
        val delta: Float = it.changes.first().scrollDelta.y
        coroutine_scope.launch {
            state.scrollBy(50f * (if (reverse_direction) -delta else delta))
        }
    }
}
