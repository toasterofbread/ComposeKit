package dev.toastbits.composekit.test

import kotlin.reflect.KFunction1

interface ReceiverCheckTestScope<T> {
    fun assertTrue(input: T)
    fun assertFalse(input: T)
}

fun <T> testReceiverCheck(function: KFunction1<T, Boolean>, test: ReceiverCheckTestScope<T>.() -> Unit) {
    val scope: ReceiverCheckTestScope<T> =
        object : ReceiverCheckTestScope<T> {
            override fun assertTrue(input: T) {
                assertTrueWith(input, function)
            }

            override fun assertFalse(input: T) {
                assertFalseWith(input, function)
            }
        }

    test(scope)
}
