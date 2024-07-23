package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues

@Composable
actual fun SwipeRefresh(
    state: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier,
    swipe_enabled: Boolean,
    indicator: Boolean,
    indicator_padding: PaddingValues,
    content: @Composable () -> Unit
) {
    Box(modifier) {
        content()
    }
}
