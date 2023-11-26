package com.toasterofbread.composekit.platform

import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs

actual fun getPlatform(): Platform =
    Platform.DESKTOP

actual fun getPlatformForbiddenFilenameCharacters(): String =
    when (hostOs) {
        OS.Android, OS.Linux -> "/"
        OS.Windows -> "<>:\"/\\|?*"
        OS.MacOS -> ":/"
        else -> throw NotImplementedError(hostOs.name)
    }
