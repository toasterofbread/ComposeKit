package dev.toastbits.composekit.platform.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

private val true_state: State<Boolean> =
    object : State<Boolean> {
        override val value: Boolean = false
    }

@Composable
actual fun rememberKeyboardOpen(): State<Boolean> = true_state
