package dev.toastbits.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import okio.Sink
import okio.Source

actual open class PlatformContext(
    actual val coroutine_scope: CoroutineScope
) {
    actual fun getFilesDir(): PlatformFile? = null
    actual fun getCacheDir(): PlatformFile? = null

    actual fun promptUserForDirectory(
        persist: Boolean,
        callback: (uri: String?) -> Unit
    ) {
        callback(null)
    }

    actual fun promptUserForFile(
        mime_types: Set<String>,
        persist: Boolean,
        callback: (uri: String?) -> Unit
    ) {
        callback(null)
    }

    actual fun promptUserForFileCreation(
        mime_type: String,
        filename_suggestion: String?,
        persist: Boolean,
        callback: (uri: String?) -> Unit
    ) {
        callback(null)
    }

    actual fun getUserDirectoryFile(uri: String): PlatformFile? = null

    actual fun isAppInForeground(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setStatusBarColour(colour: Color?) {}

    actual fun setNavigationBarColour(colour: Color?) {}

    actual fun isDisplayingAboveNavigationBar(): Boolean = false

    actual fun getLightColorScheme(): ColorScheme = lightColorScheme()

    actual fun getDarkColorScheme(): ColorScheme = darkColorScheme()

    actual fun canShare(): Boolean = false

    actual fun shareText(text: String, title: String?) {
        throw IllegalStateException()
    }

    actual fun canOpenUrl(): Boolean = true

    actual fun openUrl(url: String) {
        jsWindowOpen(url)
    }

    actual fun canCopyText(): Boolean = true

    actual fun copyText(text: String) {
        jsClipboardCopy(text)
    }

    actual fun canSendNotifications(): Boolean = true

    actual fun sendNotification(title: String, body: String) {
        jsWindowAlert("$title - $body")
    }

    actual fun sendNotification(throwable: Throwable) {
        sendNotification(throwable::class.simpleName ?: "Error", throwable.message ?: "")
    }

    actual fun sendToast(text: String, long: Boolean) {
        sendNotification(text, "")
    }

    actual fun vibrate(duration: Double) {}

    actual fun isConnectionMetered(): Boolean = false
}

private fun jsWindowOpen(url: String) {
    js("window.open(url, '_blank').focus();")
}

private fun jsClipboardCopy(text: String) {
    js("navigator.clipboard.writeText(text);")
}

private fun jsWindowAlert(text: String) {
    js("window.alert(text);")
}

actual class PlatformFile{
    init {
        throw IllegalStateException()
    }

    actual val uri: String
        get() = throw IllegalStateException()
    actual val name: String
        get() = throw IllegalStateException()
    actual val path: String
        get() = throw IllegalStateException()
    actual val absolute_path: String
        get() = throw IllegalStateException()
    actual val parent_file: PlatformFile
        get() = throw IllegalStateException()
    actual val exists: Boolean
        get() = throw IllegalStateException()
    actual val is_directory: Boolean
        get() = throw IllegalStateException()
    actual val is_file: Boolean
        get() = throw IllegalStateException()

    actual fun getRelativePath(relative_to: PlatformFile): String = throw IllegalStateException()

    actual fun inputStream(): Source = throw IllegalStateException()

    actual fun outputStream(append: Boolean): Sink = throw IllegalStateException()

    actual fun listFiles(): List<PlatformFile>? = throw IllegalStateException()

    actual fun resolve(relative_path: String): PlatformFile = throw IllegalStateException()

    actual fun getSibling(sibling_name: String): PlatformFile = throw IllegalStateException()

    actual fun delete(): Boolean = throw IllegalStateException()

    actual fun createFile(): Boolean = throw IllegalStateException()

    actual fun mkdirs(): Boolean = throw IllegalStateException()

    actual fun renameTo(new_name: String): PlatformFile = throw IllegalStateException()

    actual fun moveTo(destination: PlatformFile) { throw IllegalStateException() }

    actual fun moveDirContentTo(destination: PlatformFile): Result<PlatformFile> = throw IllegalStateException()

    actual fun matches(other: PlatformFile): Boolean = throw IllegalStateException()

    actual companion object
}
