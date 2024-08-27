package dev.toastbits.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope

actual open class PlatformContextImpl(
    actual override val coroutine_scope: CoroutineScope
): PlatformContext {
    actual override fun getFilesDir(): PlatformFile? = null
    actual override fun getCacheDir(): PlatformFile? = null

    actual override fun promptUserForDirectory(
        persist: Boolean,
        callback: (uri: String?) -> Unit
    ) {
        callback(null)
    }

    actual override fun promptUserForFile(
        mime_types: Set<String>,
        persist: Boolean,
        callback: (uri: String?) -> Unit
    ) {
        callback(null)
    }

    actual override fun promptUserForFileCreation(
        mime_type: String,
        filename_suggestion: String?,
        persist: Boolean,
        callback: (uri: String?) -> Unit
    ) {
        callback(null)
    }

    actual override fun getUserDirectoryFile(uri: String): PlatformFile? = null

    actual override fun isAppInForeground(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun setStatusBarColour(colour: Color?) {}

    actual override fun setNavigationBarColour(colour: Color?) {}

    actual override fun isDisplayingAboveNavigationBar(): Boolean = false

    actual override fun getLightColorScheme(): ColorScheme = lightColorScheme()

    actual override fun getDarkColorScheme(): ColorScheme = darkColorScheme()

    actual override fun canShare(): Boolean = false

    actual override fun shareText(text: String, title: String?) {
        throw IllegalStateException()
    }

    actual override fun canOpenUrl(): Boolean = true

    actual override fun openUrl(url: String) {
        jsWindowOpen(url)
    }

    actual override fun canCopyText(): Boolean = true

    actual override fun copyText(text: String) {
        jsClipboardCopy(text)
    }

    actual override fun canSendNotifications(): Boolean = true

    actual override fun sendNotification(title: String, body: String) {
        jsWindowAlert("$title - $body")
    }

    actual override fun sendNotification(throwable: Throwable) {
        sendNotification(throwable::class.simpleName ?: "Error", throwable.message ?: "")
    }

    actual override fun sendToast(text: String, long: Boolean) {
        sendNotification(text, "")
    }

    actual override fun vibrate(duration: Double) {}

    actual override fun isConnectionMetered(): Boolean = false
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
