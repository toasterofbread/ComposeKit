package dev.toastbits.composekit.platform

internal actual fun getPlatform(): Platform = Platform.WEB

actual fun getPlatformForbiddenFilenameCharacters(): String = ""

actual fun getPlatformOSName(): String = js("window.navigator.platform")

actual fun getPlatformHostName(): String = "Unknown"

actual fun assert(condition: Boolean) {}

actual inline fun lazyAssert(
    noinline getMessage: (() -> String)?,
    condition: () -> Boolean
) {
}

actual fun getEnv(name: String): String? = null
