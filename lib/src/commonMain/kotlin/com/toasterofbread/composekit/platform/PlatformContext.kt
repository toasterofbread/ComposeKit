package com.toasterofbread.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

expect open class PlatformContext {
    fun getFilesDir(): File
    fun getCacheDir(): File

    fun promptUserForDirectory(persist: Boolean = false, callback: (uri: String?) -> Unit)
    fun promptUserForFile(mime_types: Set<String>, persist: Boolean = false, callback: (uri: String?) -> Unit)
    fun promptUserForFileCreation(mime_type: String, filename_suggestion: String?, persist: Boolean = false, callback: (uri: String?) -> Unit)
    fun getUserDirectoryFile(uri: String): PlatformFile

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

    fun canSendNotifications(): Boolean
    fun sendNotification(title: String, body: String)
    fun sendNotification(throwable: Throwable)

    fun sendToast(text: String, long: Boolean = false)

    fun vibrate(duration: Double)

    fun deleteFile(name: String): Boolean
    fun openFileInput(name: String): FileInputStream
    fun openFileOutput(name: String, append: Boolean = false): FileOutputStream

    fun openResourceFile(path: String): InputStream
    fun listResourceFiles(path: String): List<String>?

    fun loadFontFromFile(path: String): Font

    fun isConnectionMetered(): Boolean
}

expect class PlatformFile {
    val uri: String
    val name: String
    val path: String
    val absolute_path: String

    val exists: Boolean
    val is_directory: Boolean
    val is_file: Boolean

    fun getRelativePath(relative_to: PlatformFile): String
    fun inputStream(): InputStream
    fun outputStream(append: Boolean = false): OutputStream

    fun listFiles(): List<PlatformFile>?
    fun resolve(relative_path: String): PlatformFile
    fun getSibling(sibling_name: String): PlatformFile

    fun delete(): Boolean
    fun createFile(): Boolean
    fun mkdirs(): Boolean
    fun renameTo(new_name: String): PlatformFile
//    fun copyTo(destination: PlatformFile)
//    fun delete()
    fun moveDirContentTo(destination: PlatformFile): Result<PlatformFile>

    fun matches(other: PlatformFile): Boolean

    companion object {
        fun fromFile(file: File, context: PlatformContext): PlatformFile
    }
}

fun PlatformContext.vibrateShort() {
    vibrate(0.01)
}
