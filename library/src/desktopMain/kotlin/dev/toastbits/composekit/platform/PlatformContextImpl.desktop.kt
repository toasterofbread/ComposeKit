package dev.toastbits.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.badlogic.gdx.files.FileHandle
import com.sshtools.twoslices.Toast
import com.sshtools.twoslices.ToastType
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent
import games.spooky.gdx.nativefilechooser.desktop.DesktopFileChooser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.URI

actual open class PlatformContextImpl(
    private val app_name: String,
    actual override val coroutine_scope: CoroutineScope
): PlatformContext {
    open suspend fun getIconImageData(): ByteArray? = null

    private val file_chooser: DesktopFileChooser = DesktopFileChooser()
    private fun getFileChooserConfiguration(): NativeFileChooserConfiguration =
        NativeFileChooserConfiguration().apply {
            directory = FileHandle(getHomeDir())
        }

    actual override fun getFilesDir(): PlatformFile? =
        PlatformFile.fromFile(getDesktopFilesDir(app_name), this)

    actual override fun getCacheDir(): PlatformFile? {
        val subdir: String = when (hostOs) {
            OS.Linux -> ".cache"
            OS.Windows -> return (getFilesDir() ?: return null).resolve("cache")
            else -> throw NotImplementedError(hostOs.name)
        }

        val file: File = getHomeDir().resolve(subdir).resolve(app_name.lowercase())
        return PlatformFile.fromFile(file, this)
    }

    private fun getTempDir(): File {
        val dir: File = when (hostOs) {
            OS.Linux -> File("/tmp")
            OS.Windows -> getHomeDir().resolve("AppData/Local/Temp")
            else -> throw NotImplementedError(hostOs.name)
        }
        return dir.resolve(app_name.lowercase())
    }

    actual override fun isAppInForeground(): Boolean = true // TODO

    actual override fun getLightColorScheme(): ColorScheme = lightColorScheme()

    actual override fun getDarkColorScheme(): ColorScheme = darkColorScheme()

    actual override fun canOpenUrl(): Boolean = Desktop.isDesktopSupported() && Desktop.getDesktop()
        .isSupported(Desktop.Action.BROWSE)

    actual override fun openUrl(url: String) {
        check(canOpenUrl())
        Desktop.getDesktop().browse(URI(url))
    }

    actual override fun canCopyText(): Boolean = true
    actual override fun copyText(text: String) {
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(text), null)
    }

    actual override fun canSendNotifications(): Boolean = true

    actual override fun sendNotification(title: String, body: String) {
        val icon_path: String = getIconFile()?.absolutePath ?: ""
        Toast.toast(ToastType.INFO, icon_path, title, body)
    }

    actual override fun sendNotification(throwable: Throwable) {
        throwable.printStackTrace()
        sendNotification(throwable::class.simpleName ?: "Exception", throwable.stackTraceToString())
    }

    actual override fun sendToast(text: String, long: Boolean) {
        sendNotification(app_name, text)
    }

    actual override fun promptUserForDirectory(persist: Boolean, callback: (uri: String?) -> Unit) {
        val configuration: NativeFileChooserConfiguration = getFileChooserConfiguration()
        configuration.intent = NativeFileChooserIntent.SAVE

        // Not perfect, but as close as I can get with the native dialog
        file_chooser.chooseFile(
            configuration,
            createFileChooserCallback(callback),
            { dir: File, file: String ->
                dir.resolve(file).isDirectory
            }
        )
    }

    actual override fun promptUserForFile(mime_types: Set<String>, persist: Boolean, callback: (uri: String?) -> Unit) {
        file_chooser.chooseFile(
            getFileChooserConfiguration(),
            createFileChooserCallback(callback)
        )
    }

    actual override fun promptUserForFileCreation(mime_type: String, filename_suggestion: String?, persist: Boolean, callback: (uri: String?) -> Unit) {
        val configuration: NativeFileChooserConfiguration = getFileChooserConfiguration()
        configuration.intent = NativeFileChooserIntent.SAVE
        configuration.mimeFilter = mime_type
        file_chooser.chooseFile(configuration, createFileChooserCallback(callback))
    }

    actual override fun getUserDirectoryFile(uri: String): PlatformFile? {
        return PlatformFile(File(uri))
    }

    // Unsupported and/or too much work
    actual override fun isConnectionMetered(): Boolean = false
    actual override fun setNavigationBarColour(colour: Color?) {}
    actual override fun isDisplayingAboveNavigationBar(): Boolean = false
    actual override fun vibrate(duration: Double) {}
    actual override fun setStatusBarColour(colour: Color?) {}
    actual override fun canShare(): Boolean = false
    actual override fun shareText(text: String, title: String?): Unit = throw NotImplementedError()

    @Suppress("NewApi")
    private fun getIconFile(): File? {
        val file: File = getTempDir().resolve("ic_$app_name.png")
        if (!file.isFile) {
            val image_data: ByteArray =
                runBlocking { getIconImageData() } ?: return null

            file.parentFile.mkdirs()
            file.writeBytes(image_data)
        }
        return file
    }

    private fun createFileChooserCallback(callback: (uri: String?) -> Unit) =
        object : NativeFileChooserCallback {
            override fun onFileChosen(file: FileHandle) {
                callback(file.file().absolutePath)
            }

            override fun onCancellation() {
                callback(null)
            }

            override fun onError(exception: Exception?) {
                exception?.printStackTrace()
                callback(null)
            }
        }
}

private fun getHomeDir(): File = File(System.getProperty("user.home"))

actual fun PlatformFile.Companion.fromFile(file: File, context: PlatformContext): PlatformFile =
    PlatformFile(file)

fun getDesktopFilesDir(app_name: String): File {
    val subdir: String =
        when (hostOs) {
            OS.Linux -> ".local/share"
            OS.Windows -> "AppData/Local/"
            else -> throw NotImplementedError(hostOs.name)
        }

    return getHomeDir().resolve(subdir).resolve(app_name.lowercase())
}
