package dev.toastbits.composekit.platform.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues

@Composable
expect fun SwipeRefresh(
    state: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    swipe_enabled: Boolean = true,
    indicator: Boolean = true,
    indicator_padding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit
)