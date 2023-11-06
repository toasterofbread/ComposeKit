package com.toasterofbread.toastercomposetools.platform.composable

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

actual fun Modifier.scrollWheelScrollable(
    state: ScrollableState,
    reverse_direction: Boolean
): Modifier = composed {
    return@composed mouseWheelInput { direction ->
        state.scrollBy(50f * (if (reverse_direction) -direction else direction))
        return@mouseWheelInput true
    }
}
