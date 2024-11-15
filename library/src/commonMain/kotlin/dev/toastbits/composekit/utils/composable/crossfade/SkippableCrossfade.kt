package dev.toastbits.composekit.utils.composable.crossfade

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun <T> SkippableCrossfade(
    state: T,
    shouldSkipTransition: (T, T) -> Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    var crossfadeState: T by remember { mutableStateOf(state) }
    var currentState: T by remember { mutableStateOf(state) }
    var useCurrentState: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (shouldSkipTransition(currentState, state)) {
            currentState = state
            useCurrentState = true
        }
        else {
            currentState = state
            crossfadeState = state
            useCurrentState = false
        }
    }

    Crossfade(crossfadeState, modifier) { s ->
        content(if (useCurrentState) currentState else s)
    }
}
