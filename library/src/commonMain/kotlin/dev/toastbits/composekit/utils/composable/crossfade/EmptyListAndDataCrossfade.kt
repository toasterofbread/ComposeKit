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
