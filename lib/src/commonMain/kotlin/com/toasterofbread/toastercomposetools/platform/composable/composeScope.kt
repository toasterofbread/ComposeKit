package com.toasterofbread.toastercomposetools.platform.composable

import androidx.compose.runtime.Composable

@Composable
fun composeScope(block: @Composable () -> Unit) {
    block()
}

@Composable
fun <A, B, C, D> composeScope(p1: A, p2: B, p3: C, p4: D, block: @Composable (A, B, C, D) -> Unit) {
    block(p1, p2, p3, p4)
}
