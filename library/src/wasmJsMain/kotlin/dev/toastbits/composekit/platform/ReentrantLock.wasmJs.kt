package dev.toastbits.composekit.platform

actual class ReentrantLock {
    actual fun lock() {}
    actual fun unlock() {}
    actual fun tryLock(): Boolean = true

    actual inline fun <T> withLock(block: () -> T): T = block()
}
