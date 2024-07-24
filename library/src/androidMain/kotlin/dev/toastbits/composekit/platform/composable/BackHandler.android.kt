package dev.toastbits.composekit.platform.composable

import androidx.compose.runtime.Composable
import dev.toastbits.composekit.platform.ApplicationContext
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
    val application_context: ApplicationContext =
        context.application_context
        ?: throw RuntimeException("Cannot simulate back press because application_context is null")
    application_context.simulateBackPress()
    return true
}
