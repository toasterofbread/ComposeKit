package dev.toastbits.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import okio.FileHandle
import okio.Sink
import okio.Source

expect open class PlatformContext {
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

expect class PlatformFile {
    val uri: String
    val name: String
    val path: String
    val absolute_path: String
    val parent_file: PlatformFile

    val exists: Boolean
    val is_directory: Boolean
    val is_file: Boolean

    fun getRelativePath(relative_to: PlatformFile): String
    fun inputStream(): Source
    fun outputStream(append: Boolean = false): Sink

    fun listFiles(): List<PlatformFile>?
    fun resolve(relative_path: String): PlatformFile
    fun getSibling(sibling_name: String): PlatformFile

    fun delete(): Boolean
    fun createFile(): Boolean
    fun mkdirs(): Boolean
    fun renameTo(new_name: String): PlatformFile
    fun moveTo(destination: PlatformFile)
    fun moveDirContentTo(destination: PlatformFile): Result<PlatformFile>

    fun matches(other: PlatformFile): Boolean

     companion object
}

fun PlatformContext.vibrateShort() {
    vibrate(0.01)
}
