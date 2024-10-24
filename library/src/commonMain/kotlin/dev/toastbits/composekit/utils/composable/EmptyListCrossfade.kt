package dev.toastbits.composekit.utils.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
fun <T> EmptyListCrossfade(
    list: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (list: List<T>?) -> Unit,
) {
    var current_list: List<T> by remember { mutableStateOf(list) }
    LaunchedEffect(list) {
        if (list.isNotEmpty()) {
            current_list = list
        }
    }

    Crossfade(list.isEmpty(), modifier) { is_empty ->
        content(if (is_empty) null else current_list)
    }
}

@Composable
fun <T, D> EmptyListAndDataCrossfade(
    list: List<T>,
    data: D,
    modifier: Modifier = Modifier,
    content: @Composable (list: List<T>?, data: D) -> Unit,
) {
    var current_list: List<T> by remember { mutableStateOf(list) }
    LaunchedEffect(list) {
        if (list.isNotEmpty()) {
            current_list = list
        }
    }

    Crossfade(Pair(list.isEmpty(), data), modifier) {
        content(if (it.first) null else current_list, it.second)
    }
}

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
