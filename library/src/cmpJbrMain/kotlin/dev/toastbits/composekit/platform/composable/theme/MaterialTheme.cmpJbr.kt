package dev.toastbits.composekit.platform.composable.theme

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.settings.ui.ThemeValues

@Composable
internal actual fun ThemeValues.PlatformTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalScrollbarStyle provides LocalScrollbarStyle.current.copy(
            hoverColor = accent,
            unhoverColor = accent.copy(alpha = accent.alpha * 0.25f),
            thickness = 6.dp
        )
    ) {
        content()
    }
}
