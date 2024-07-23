package dev.toastbits.composekit.platform.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
expect fun rememberKeyboardOpen(): State<Boolean>
