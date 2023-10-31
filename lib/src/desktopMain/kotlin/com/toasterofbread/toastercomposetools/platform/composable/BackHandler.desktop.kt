package com.toasterofbread.toastercomposetools.platform.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

private abstract class Listener(
    var enabled: Boolean
) {
    abstract fun onBackPressed()
}

private val listeners: MutableList<Listener> = mutableListOf()

@Composable
actual fun BackHandler(enabled: Boolean, action: () -> Unit) {
    val listener = remember {
        object : Listener(enabled) {
            override fun onBackPressed() {
                action()
            }
        }
    }

    LaunchedEffect(enabled) {
        listener.enabled = enabled
    }

    DisposableEffect(Unit) {
        listeners.add(listener)

        onDispose {
            listeners.remove(listener)
        }
    }
}

fun onWindowBackPressed(): Boolean {
    for (listener in listeners.reversed()) {
        if (listener.enabled) {
            listener.onBackPressed()
            return true
        }
    }
    return false
}
