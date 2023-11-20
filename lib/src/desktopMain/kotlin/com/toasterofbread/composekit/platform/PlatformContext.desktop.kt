package com.toasterofbread.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.platform.Font
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.awt.Desktop
import java.awt.Dimension
import java.awt.Window
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.security.CodeSource
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

private fun getHomeDir(): File = File(System.getProperty("user.home"))

actual open class PlatformContext(private val app_name: String, private val resource_class: Class<*>) {
    actual fun getFilesDir(): File {
        val subdir = when (hostOs) {
            OS.Linux -> ".local/share"
            OS.Windows -> TODO()
            OS.MacOS -> TODO()
            else -> throw NotImplementedError(hostOs.name)
        }
        return getHomeDir().resolve(subdir).resolve(app_name.lowercase())
    }

    actual fun getCacheDir(): File {
        val subdir = when (hostOs) {
            OS.Linux -> ".cache"
            OS.Windows -> TODO()
            OS.MacOS -> TODO()
            else -> throw NotImplementedError(hostOs.name)
        }
        return getHomeDir().resolve(subdir).resolve(app_name.lowercase())
    }

    actual fun isAppInForeground(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setStatusBarColour(colour: Color?) {}

    actual fun getLightColorScheme(): ColorScheme = lightColorScheme()

    actual fun getDarkColorScheme(): ColorScheme = darkColorScheme()

    actual fun canShare(): Boolean = false

    actual fun shareText(text: String, title: String?) {
        throw NotImplementedError()
    }

    actual fun canOpenUrl(): Boolean = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)

    actual fun openUrl(url: String) {
        assert(canOpenUrl())
        Desktop.getDesktop().browse(URI(url))
    }

    actual fun canSendNotifications(): Boolean {
        TODO()
    }

    actual fun sendNotification(title: String, body: String) {
    }

    actual fun sendNotification(throwable: Throwable) {
    }

    actual fun sendToast(text: String, long: Boolean) {
    }

    actual fun vibrate(duration: Double) {}

    actual fun openFileInput(name: String): FileInputStream =
        getFilesDir().resolve(name).inputStream()

    actual fun openFileOutput(name: String, append: Boolean): FileOutputStream {
        val path = getFilesDir().resolve(name)
        path.createNewFile()
        return path.outputStream()
    }

    private fun getResourceDir(): File = File("/assets")

    actual fun openResourceFile(path: String): InputStream {
        val resource_path: String = getResourceDir().resolve(path).path

        val stream: InputStream? = resource_class.getResourceAsStream(resource_path)
        checkNotNull(stream) { "Could not open resource at $resource_path" }

        return stream
    }

    @Suppress("NewApi")
    actual fun listResourceFiles(path: String): List<String>? {
        val paths: MutableList<String> = mutableListOf()

        val src: CodeSource = resource_class.protectionDomain.codeSource
        val zip: ZipInputStream = ZipInputStream(src.location.openStream())

        val resource_dir: File = getResourceDir()
        val resource_path: String = resource_dir.resolve(path).path.trim('/') + '/'

        val searching_root: Boolean = resource_dir == File(resource_path)

        var ze: ZipEntry?
        while (zip.nextEntry.also { ze = it } != null) {
            val entry = ze!!.name.trimEnd('/')

            if (searching_root) {
                if (entry == "META-INF" || entry == "com" || entry.contains('$') || entry.endsWith(".class")) {
                    continue
                }
            }
            else if (!entry.startsWith(resource_path)) {
                continue
            }

            if (entry.length == resource_path.length) {
                continue
            }

            var has_slash: Boolean = false
            for (i in resource_path.length until entry.length) {
                if (entry[i] == '/') {
                    has_slash = true
                    break
                }
            }

            if (has_slash) {
                continue
            }

            paths.add(entry.removePrefix(resource_path))
        }

        return paths
    }

    actual fun loadFontFromFile(path: String): Font {
        val resource_path = getResourceDir().resolve(path).path

        val stream = resource_class.getResourceAsStream(resource_path)!!
        val bytes = stream.readBytes()
        stream.close()

        return Font(path, bytes)
    }

    actual fun isConnectionMetered(): Boolean {
        TODO("Not yet implemented")
    }

    private var screen_size: Dimension? by mutableStateOf(null)
    fun updateScreenSize() {
        screen_size = Window.getWindows().first().size
    }

    actual fun promptUserForDirectory(persist: Boolean, callback: (uri: String?) -> Unit) {
        TODO()
    }

    actual fun promptUserForFile(mime_types: Set<String>, persist: Boolean, callback: (uri: String?) -> Unit) {
        TODO()
    }

    actual fun promptUserForJsonCreation(filename_suggestion: String?, persist: Boolean, callback: (uri: String?) -> Unit) {
        TODO()
    }

    actual fun getUserDirectoryFile(uri: String): PlatformFile {
        TODO("Not yet implemented")
    }

    actual fun setNavigationBarColour(colour: Color?) {}

    actual fun isDisplayingAboveNavigationBar(): Boolean = false

    actual fun deleteFile(name: String): Boolean {
        TODO("Not yet implemented")
    }
}

actual class PlatformFile(private val file: File) {
    actual val uri: String
        get() = file.toURI().path
    actual val name: String
        get() = file.name
    actual val path: String
        get() = file.path
    actual val absolute_path: String
        get() = file.absolutePath
    actual val exists: Boolean
        get() = file.exists()
    actual val is_directory: Boolean
        get() = file.isDirectory
    actual val is_file: Boolean
        get() = file.isFile

    actual fun getRelativePath(relative_to: PlatformFile): String {
        return file.relativeTo(relative_to.file).path
    }

    actual fun inputStream(): InputStream {
        return file.inputStream()
    }

    actual fun outputStream(append: Boolean): OutputStream {
        return FileOutputStream(file, append)
    }

    actual fun listFiles(): List<PlatformFile>? {
        return file.listFiles()?.map { PlatformFile(it) }
    }

    actual fun resolve(relative_path: String): PlatformFile {
        return PlatformFile(file.resolve(relative_path))
    }

    actual fun getSibling(sibling_name: String): PlatformFile {
        return PlatformFile(file.resolveSibling(sibling_name))
    }

    actual fun delete(): Boolean {
        return file.delete()
    }

    actual fun createFile(): Boolean {
        return file.createNewFile()
    }

    actual fun mkdirs(): Boolean {
        return file.mkdirs()
    }

    actual fun renameTo(new_name: String): PlatformFile {
        val dest = getSibling(new_name)
        file.renameTo(dest.file)
        return dest
    }

    actual fun moveDirContentTo(destination: PlatformFile): Result<PlatformFile> {
        TODO()
    }

    actual companion object {
        actual fun fromFile(
            file: File,
            context: PlatformContext,
        ): PlatformFile {
            return PlatformFile(file)
        }
    }

    actual fun matches(other: PlatformFile): Boolean {
        return file == other.file
    }
}
