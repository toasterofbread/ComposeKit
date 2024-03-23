package com.toasterofbread.composekit.platform.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.toasterofbread.composekit.platform.PlatformContext

@Composable
actual fun BackHandler(
    enabled: Boolean,
    priority: Int,
    action: () -> Unit
) {
    val listener: Listener = remember {
        object : Listener(enabled, priority) {
            override fun onBackPressed() {
                action()
            }
        }
    }

    LaunchedEffect(enabled) {
        listener.enabled = enabled
    }

    LaunchedEffect(priority) {
        listener.priority = priority
    }

    DisposableEffect(Unit) {
        listeners.add(listener)

        onDispose {
            listeners.remove(listener)
        }
    }
}

actual fun onWindowBackPressed(context: PlatformContext): Boolean {
    var highest_priority_listener: Listener? = null

    for (listener in listeners.reversed()) {
        if (!listener.enabled) {
            continue
        }

        if (highest_priority_listener == null || listener.priority > highest_priority_listener.priority) {
            highest_priority_listener = listener
        }
    }

    if (highest_priority_listener != null) {
        highest_priority_listener.onBackPressed()
        return true
    }

    return false
}

private val listeners: MutableList<Listener> = mutableListOf()

private abstract class Listener(
    var enabled: Boolean,
    var priority: Int
) {
    abstract fun onBackPressed()
}
