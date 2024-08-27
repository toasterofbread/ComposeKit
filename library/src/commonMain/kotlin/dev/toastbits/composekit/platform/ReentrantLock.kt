package dev.toastbits.composekit.platform

expect class ReentrantLock() {
    fun lock()
    fun unlock()
    fun tryLock(): Boolean

    inline fun <T> withLock(block: () -> T): T
}
