package dev.toastbits.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope

val LocalContext: ProvidableCompositionLocal<PlatformContext> =
    staticCompositionLocalOf { throw IllegalStateException() }

expect interface PlatformContext {
    val coroutine_scope: CoroutineScope

    fun getFilesDir(): PlatformFile?
    fun getCacheDir(): PlatformFile?

    fun promptUserForDirectory(persist: Boolean = false, callback: (uri: String?) -> Unit)
    fun promptUserForFile(mime_types: Set<String>, persist: Boolean = false, callback: (uri: String?) -> Unit)
    fun promptUserForFileCreation(mime_type: String, filename_suggestion: String?, persist: Boolean = false, callback: (uri: String?) -> Unit)
    fun getUserDirectoryFile(uri: String): PlatformFile?

    fun isAppInForeground(): Boolean

    fun setStatusBarColour(colour: Color?)

    fun setNavigationBarColour(colour: Color?)
    fun isDisplayingAboveNavigationBar(): Boolean

    fun getLightColorScheme(): ColorScheme
    fun getDarkColorScheme(): ColorScheme

    fun canShare(): Boolean
    fun shareText(text: String, title: String? = null)

    fun canOpenUrl(): Boolean
    fun openUrl(url: String)

    fun canCopyText(): Boolean
    fun copyText(text: String)

    fun canSendNotifications(): Boolean
    fun sendNotification(title: String, body: String)
    fun sendNotification(throwable: Throwable)

    fun sendToast(text: String, long: Boolean = false)

    fun vibrate(duration: Double)

    fun isConnectionMetered(): Boolean
}

fun PlatformContext.vibrateShort() {
    vibrate(0.01)
}
