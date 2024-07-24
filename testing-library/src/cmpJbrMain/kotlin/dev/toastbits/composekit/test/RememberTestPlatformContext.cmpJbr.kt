package dev.toastbits.composekit.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.mokkery.mock
import dev.toastbits.composekit.platform.PlatformContext

@Composable
actual fun rememberTestPlatformContext(): PlatformContext {
    return remember { mock() }
}