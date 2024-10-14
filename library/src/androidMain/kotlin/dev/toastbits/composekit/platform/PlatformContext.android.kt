package dev.toastbits.composekit.platform

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope

actual interface PlatformContext {
    val ctx: Context
    val application_context: ApplicationContext?

    actual val coroutine_scope: CoroutineScope
    actual fun getFilesDir(): PlatformFile?
    actual fun getCacheDir(): PlatformFile?
    actual fun promptUserForDirectory(
        persist: Boolean,
        callback: (uri: String?) -> Unit
    )

    actual fun promptUserForFile(
        mime_types: Set<String>,
        persist: Boolean,
        callback: (uri: String?) -> Unit
    )

    actual fun promptUserForFileCreation(
        mime_type: String,
        filename_suggestion: String?,
        persist: Boolean,
        callback: (uri: String?) -> Unit
    )

    actual fun getUserDirectoryFile(uri: String): PlatformFile?
    actual fun isAppInForeground(): Boolean
    actual fun setStatusBarColour(colour: Color?)
    actual fun setNavigationBarColour(colour: Color?)
    actual fun isDisplayingAboveNavigationBar(): Boolean
    actual fun getLightColorScheme(): ColorScheme
    actual fun getDarkColorScheme(): ColorScheme
    actual fun canShare(): Boolean
    actual fun shareText(text: String, title: String?)
    actual fun canOpenUrl(): Boolean
    actual fun openUrl(url: String)
    actual fun canCopyText(): Boolean
    actual fun copyText(text: String)
    actual fun canSendNotifications(): Boolean
    actual fun sendNotification(title: String, body: String)
    actual fun sendNotification(throwable: Throwable)
    actual fun sendToast(text: String, long: Boolean)
    actual fun vibrate(duration: Double)
    actual fun isConnectionMetered(): Boolean
}
