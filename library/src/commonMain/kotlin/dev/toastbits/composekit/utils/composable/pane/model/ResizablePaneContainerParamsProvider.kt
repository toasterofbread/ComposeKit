package dev.toastbits.composekit.utils.composable.pane.model

import androidx.compose.runtime.Composable
import dev.toastbits.composekit.platform.preferences.impl.ComposeKitSettings
import dev.toastbits.composekit.platform.preferences.impl.LocalComposeKitSettings
import dev.toastbits.composekit.platform.preferences.impl.group.getResizablePaneContainerParams

interface ResizablePaneContainerParamsProvider {
    @Composable
    operator fun invoke(): ResizablePaneContainerParams

    companion object {
        fun default(): ResizablePaneContainerParamsProvider =
            object : ResizablePaneContainerParamsProvider {
                @Composable
                override fun invoke(): ResizablePaneContainerParams {
                    val settings: ComposeKitSettings? = LocalComposeKitSettings.current
                    val default: ResizablePaneContainerParams = ResizablePaneContainerParamsData()
                    return (
                        settings?.Interface?.getResizablePaneContainerParams(default)
                        ?: default
                    )
                }
            }
    }
}
