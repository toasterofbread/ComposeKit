package dev.toastbits.composekit.platform

actual fun getPlatform(): Platform =
    Platform.ANDROID

actual fun getPlatformForbiddenFilenameCharacters(): String = "/"
