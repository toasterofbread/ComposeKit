package dev.toastbits.composekit.utils.composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable

@Composable
fun BoxWithOptionalConstraints(
    enable_constraints: Boolean,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable (BoxWithConstraintsScope?) -> Unit
) {
    if (enable_constraints) {
        BoxWithConstraints(modifier, contentAlignment = contentAlignment) {
            content(this)
        }
    }
    else {
        Box(modifier, contentAlignment = contentAlignment) {
            content(null)
        }
    }
}