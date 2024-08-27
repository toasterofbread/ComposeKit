package dev.toastbits.composekit.platform

import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.net.InetAddress

actual fun getPlatform(): Platform =
    Platform.DESKTOP

actual fun getPlatformForbiddenFilenameCharacters(): String =
    when (hostOs) {
        OS.Android, OS.Linux -> "/"
        OS.Windows -> "<>:\"/\\|?*"
        OS.MacOS -> ":/"
        else -> throw NotImplementedError(hostOs.name)
    }

actual fun getPlatformOSName(): String = System.getProperty("os.name")
actual fun getPlatformHostName(): String = InetAddress.getLocalHost().hostName
