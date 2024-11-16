package dev.toastbits.composekit.utils.modifier

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.ui.Modifier

fun Modifier.scrollWithoutClip(
    state: ScrollState,
    is_vertical: Boolean,
    reverse_scrolling: Boolean = false,
    fling_behaviour: FlingBehavior? = null,
    is_scrollable: Boolean = true
): Modifier = TODO()
