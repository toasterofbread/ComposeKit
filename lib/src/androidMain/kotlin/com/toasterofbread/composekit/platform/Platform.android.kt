package com.toasterofbread.composekit.platform

actual fun getPlatform(): Platform =
    Platform.ANDROID

actual fun getPlatformForbiddenFilenameCharacters(): String = "/"
