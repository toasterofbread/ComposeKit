package dev.toastbits.composekit.platform

import kotlin.contracts.*

@OptIn(kotlin.contracts.ExperimentalContracts::class)
inline fun <R> synchronized(lock: Any, block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return synchronizedImpl(lock, block)
}

expect inline fun <R> synchronizedImpl(lock: Any, block: () -> R): R
