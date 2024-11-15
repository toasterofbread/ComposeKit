package dev.toastbits.composekit.utils.composable.animatedvisibility

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun <T: Any> NullableValueAnimatedVisibility(
    value: T?,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    content: @Composable (value: T?) -> Unit
) {
    var current_value: T? by remember { mutableStateOf(value) }
    var show: Boolean by remember { mutableStateOf(value != null) }

    LaunchedEffect(value) {
        if (value != null) {
            current_value = value
        }
        show = value != null
    }

    AnimatedVisibility(
        show,
        modifier,
        enter = enter,
        exit = exit
    ) {
        content(current_value)
    }
}
