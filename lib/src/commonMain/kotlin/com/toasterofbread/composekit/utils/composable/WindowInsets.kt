package com.toasterofbread.composekit.utils.composable

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun WindowInsets.getStart(): Dp = with (LocalDensity.current) {
    getLeft(this, LayoutDirection.Ltr).toDp()
}

@Composable
fun WindowInsets.getEnd(): Dp = with (LocalDensity.current) {
    getRight(this, LayoutDirection.Ltr).toDp()
}

@Composable
fun WindowInsets.getTop(): Dp = with (LocalDensity.current) {
    getTop(this).toDp()
}

@Composable
fun WindowInsets.getBottom(): Dp = with (LocalDensity.current) {
    getBottom(this).toDp()
}

@Composable
fun WindowInsets.Companion.getTop(): Dp = systemBars.getTop()

@Composable
fun WindowInsets.Companion.getStart(): Dp = systemBars.getStart()

@Composable
fun WindowInsets.Companion.getEnd(): Dp = systemBars.getEnd()
