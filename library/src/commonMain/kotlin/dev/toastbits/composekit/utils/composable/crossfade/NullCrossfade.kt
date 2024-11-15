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
fun <T: Any> NullCrossfade(
    value: T?,
    modifier: Modifier = Modifier,
    content: @Composable (value: T?) -> Unit
) {
    var current_value: T? by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        if (value != null) {
            current_value = value
        }
    }

    Crossfade(value == null, modifier) { is_null ->
        content(if (is_null) null else current_value)
    }
}
