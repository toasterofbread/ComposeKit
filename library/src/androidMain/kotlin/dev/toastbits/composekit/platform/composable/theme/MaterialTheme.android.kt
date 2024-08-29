package dev.toastbits.composekit.platform.composable.theme

import androidx.compose.runtime.Composable
import dev.toastbits.composekit.settings.ui.ThemeValues

@Composable
internal actual fun ThemeValues.PlatformTheme(content: @Composable () -> Unit) {
    content()
}
