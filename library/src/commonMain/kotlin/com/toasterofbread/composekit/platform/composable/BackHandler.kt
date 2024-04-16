package dev.toastbits.composekit.platform.composable

import androidx.compose.runtime.Composable
import dev.toastbits.composekit.platform.PlatformContext

@Composable
expect fun BackHandler(
    enabled: Boolean = true,
    priority: Int = 0,
    action: () -> Unit
)

@Composable
fun BackHandler(
    getEnabled: @Composable () -> Boolean,
    priority: Int = 0,
    action: () -> Unit
) {
    BackHandler(getEnabled(), priority, action)
}

expect fun onWindowBackPressed(context: PlatformContext): Boolean
