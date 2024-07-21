package dev.toastbits.composekit.platform

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

actual class ReentrantLock {
    private val lock: ReentrantLock = ReentrantLock()

    actual fun lock() = lock.lock()
    actual fun unlock() = lock.unlock()
    actual fun tryLock(): Boolean = lock.tryLock()

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    actual inline fun <T> withLock(block: () -> T): T = lock.withLock(block)
}
