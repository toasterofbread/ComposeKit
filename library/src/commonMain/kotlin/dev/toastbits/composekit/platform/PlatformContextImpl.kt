package dev.toastbits.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope

expect open class PlatformContextImpl: PlatformContext {
    override val coroutine_scope: CoroutineScope
    override fun getFilesDir(): PlatformFile?
    override fun getCacheDir(): PlatformFile?
    override fun promptUserForDirectory(persist: Boolean, callback: (uri: String?) -> Unit)
    override fun promptUserForFile(mime_types: Set<String>, persist: Boolean, callback: (uri: String?) -> Unit)
    override fun promptUserForFileCreation(mime_type: String, filename_suggestion: String?, persist: Boolean, callback: (uri: String?) -> Unit)
    override fun getUserDirectoryFile(uri: String): PlatformFile?
    override fun isAppInForeground(): Boolean
    override fun setStatusBarColour(colour: Color?)
    override fun setNavigationBarColour(colour: Color?)
    override fun isDisplayingAboveNavigationBar(): Boolean
    override fun getLightColorScheme(): ColorScheme
    override fun getDarkColorScheme(): ColorScheme
    override fun canShare(): Boolean
    override fun shareText(text: String, title: String?)
    override fun canOpenUrl(): Boolean
    override fun openUrl(url: String)
    override fun canCopyText(): Boolean
    override fun copyText(text: String)
    override fun canSendNotifications(): Boolean
    override fun sendNotification(title: String, body: String)
    override fun sendNotification(throwable: Throwable)
    override fun sendToast(text: String, long: Boolean)
    override fun vibrate(duration: Double)
    override fun isConnectionMetered(): Boolean
}