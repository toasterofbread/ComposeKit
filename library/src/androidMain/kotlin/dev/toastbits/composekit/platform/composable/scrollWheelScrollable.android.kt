package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.Modifier

actual fun Modifier.scrollWheelScrollable(
    state: ScrollableState,
    reverseDirection: Boolean,
    onlyWhileShifting: Boolean
): Modifier = this
