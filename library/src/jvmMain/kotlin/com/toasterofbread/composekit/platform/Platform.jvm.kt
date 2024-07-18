@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package dev.toastbits.composekit.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

actual fun assert(condition: Boolean) {
    kotlin.assert(condition)
}

actual inline fun lazyAssert(
    noinline getMessage: (() -> String)?,
    condition: () -> Boolean
) {
    if (_Assertions.ENABLED && !condition()) {
        throw AssertionError(getMessage?.invoke() ?: "Assertion failed")
    }
}

actual fun getEnv(name: String): String? = System.getenv(name)
