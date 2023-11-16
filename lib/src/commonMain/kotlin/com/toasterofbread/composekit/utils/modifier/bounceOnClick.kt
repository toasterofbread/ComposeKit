package com.toasterofbread.composekit.utils.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.bounceOnClick() = composed {
    var pressed: Boolean by remember { mutableStateOf(false) }
    val scale: Float by animateFloatAsState(if (pressed) 0.8f else 1f)

    return@composed this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(pressed) {
            awaitPointerEventScope {
                if (pressed) {
                    waitForUpOrCancellation()
                    pressed = false
                }
                else {
                    awaitFirstDown(false)
                    pressed = true
                }
            }
        }
}
