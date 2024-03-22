package com.toasterofbread.composekit.platform.composable

import androidx.compose.runtime.Composable
import com.toasterofbread.composekit.platform.PlatformContext

@Composable
expect fun BackHandler(enabled: Boolean = true, action: () -> Unit)

@Composable
fun BackHandler(getEnabled: @Composable () -> Boolean, action: () -> Unit) {
    BackHandler(getEnabled(), action)
}

expect fun onWindowBackPressed(context: PlatformContext): Boolean
