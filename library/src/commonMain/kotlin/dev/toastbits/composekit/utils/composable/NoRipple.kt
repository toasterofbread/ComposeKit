package dev.toastbits.composekit.utils.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ripple
import androidx.compose.foundation.LocalIndication

@Composable
fun NoRipple(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalIndication provides ripple(color = Color.Transparent)) {
        content()
    }
}
