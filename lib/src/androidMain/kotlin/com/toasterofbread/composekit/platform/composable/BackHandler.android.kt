package com.toasterofbread.composekit.platform.composable

import androidx.compose.runtime.Composable
import com.toasterofbread.composekit.platform.PlatformContext

@Composable
actual fun BackHandler(enabled: Boolean, action: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled, action)
}

actual fun onWindowBackPressed(context: PlatformContext): Boolean {
    context.application_context.simulateBackPress()
}
