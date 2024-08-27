package dev.toastbits.composekit.platform.composable

import androidx.compose.runtime.Composable
import dev.toastbits.composekit.platform.PlatformContext

@Composable
actual fun BackHandler(
    enabled: Boolean,
    priority: Int,
    action: () -> Unit
) {
    androidx.activity.compose.BackHandler(enabled, action)
}

actual fun onWindowBackPressed(context: PlatformContext): Boolean {
    context.application_context?.simulateBackPress()
    return true
}
