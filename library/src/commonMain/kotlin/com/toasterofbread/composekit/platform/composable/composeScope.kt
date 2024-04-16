package dev.toastbits.composekit.platform.composable

import androidx.compose.runtime.Composable

@Composable
fun composeScope(block: @Composable () -> Unit) {
    block()
}

@Composable
fun <A> composeScope(p1: A, block: @Composable (A) -> Unit) {
    block(p1)
}

@Composable
fun <A, B> composeScope(p1: A, p2: B, block: @Composable (A, B) -> Unit) {
    block(p1, p2)
}

@Composable
fun <A, B, C> composeScope(p1: A, p2: B, p3: C, block: @Composable (A, B, C) -> Unit) {
    block(p1, p2, p3)
}

@Composable
fun <A, B, C, D> composeScope(p1: A, p2: B, p3: C, p4: D, block: @Composable (A, B, C, D) -> Unit) {
    block(p1, p2, p3, p4)
}
