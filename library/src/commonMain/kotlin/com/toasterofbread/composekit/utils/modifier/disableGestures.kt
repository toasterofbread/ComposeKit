package dev.toastbits.composekit.utils.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*

fun Modifier.disableGestures(disabled: Boolean = true): Modifier =
    if (disabled)
        pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach { it.consume() }
                }
            }
        }
    else this
