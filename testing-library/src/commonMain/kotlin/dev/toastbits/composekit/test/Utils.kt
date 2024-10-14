package dev.toastbits.composekit.test

import kotlin.test.assertTrue
import kotlin.test.assertFalse

fun <T> assertTrueWith(value: T, predicate: T.() -> Boolean) {
    assertTrue(predicate(value), "Expected predicate to be true with value:$value")
}

fun <T> assertFalseWith(value: T, predicate: T.() -> Boolean) {
    assertFalse(predicate(value), "Expected predicate to be false with value:$value")
}
