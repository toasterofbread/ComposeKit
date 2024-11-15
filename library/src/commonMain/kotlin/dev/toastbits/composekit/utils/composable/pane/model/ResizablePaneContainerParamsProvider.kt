package dev.toastbits.composekit.utils.composable.pane.model

import androidx.compose.runtime.Composable

interface ResizablePaneContainerParamsProvider {
    @Composable
    operator fun invoke(): ResizablePaneContainerParams
}
