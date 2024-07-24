package dev.toastbits.composekit.test

import androidx.compose.runtime.Composable
import dev.toastbits.composekit.platform.PlatformContext

@Composable
expect fun rememberTestPlatformContext(): PlatformContext
