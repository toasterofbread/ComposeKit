package dev.toastbits.composekit.platform.composable.theme

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.settings.ui.ThemeValues

@Composable
fun ThemeValues.ThemedLinearProgressIndicator(getProgress: () -> Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        getProgress,
        modifier,
        color = accent,
        trackColor = accent.copy(alpha = 0.2f)
    )
}

@Composable
fun ThemeValues.ThemedLinearProgressIndicator(modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        modifier,
        color = accent,
        trackColor = accent.copy(alpha = 0.2f)
    )
}
