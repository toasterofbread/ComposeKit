package dev.toastbits.composekit.platform

enum class Platform {
    ANDROID, DESKTOP;

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
