package dev.toastbits.composekit.platform

enum class Platform {
    ANDROID, DESKTOP, WEB;

    fun isCurrent(): Boolean = getPlatform() == this

    inline fun only(action: () -> Unit) {
        if (isCurrent()) {
            action()
        }
    }

    companion object {
        val current: Platform get() = getPlatform()
    }
}

internal expect fun getPlatform(): Platform
expect fun getPlatformForbiddenFilenameCharacters(): String

expect fun getPlatformOSName(): String
expect fun getPlatformHostName(): String?

expect fun assert(condition: Boolean)
expect fun assert(condition: Boolean, lazyMessage: () -> String)

expect inline fun lazyAssert(noinline getMessage: (() -> String)? = null, condition: () -> Boolean)

expect fun getEnv(name: String): String?
