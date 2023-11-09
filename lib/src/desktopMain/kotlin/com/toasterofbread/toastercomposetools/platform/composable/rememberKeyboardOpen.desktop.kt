package com.toasterofbread.composekit.platform.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun rememberKeyboardOpen(): State<Boolean> =
    remember { mutableStateOf(false) }
