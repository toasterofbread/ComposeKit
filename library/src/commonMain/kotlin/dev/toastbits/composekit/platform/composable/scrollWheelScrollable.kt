package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.Modifier

expect fun Modifier.scrollWheelScrollable(
    state: ScrollableState,
    reverseDirection: Boolean = false,
    onlyWhileShifting: Boolean = false
): Modifier
