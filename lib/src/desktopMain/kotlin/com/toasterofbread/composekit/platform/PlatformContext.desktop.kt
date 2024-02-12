package com.toasterofbread.composekit.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.platform.Font
import com.badlogic.gdx.files.FileHandle
import com.sshtools.twoslices.Toast
import com.sshtools.twoslices.ToastType
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent
import games.spooky.gdx.nativefilechooser.desktop.DesktopFileChooser
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.awt.Desktop
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.security.CodeSource
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

private fun getHomeDir(): File = File(System.getProperty("user.home"))

actual open class PlatformContext(
    private val app_name: String,
    private val icon_resource_path: String,
    private val resource_class: Class<*>
) {
    private val file_chooser: DesktopFileChooser = DesktopFileChooser()
    private fun getFileChooserConfiguration(): NativeFileChooserConfiguration =
        NativeFileChooserConfiguration().apply {
            directory = FileHandle(getHomeDir())
        }

    actual fun getFilesDir(): File {
        val subdir: String = when (hostOs) {
            OS.Linux -> ".local/share"
            OS.Windows -> "AppData/Local/"
            else -> throw NotImplementedError(hostOs.name)
        }
        return getHomeDir().resolve(subdir).resolve(app_name.lowercase())
    }

    actual fun getCacheDir(): File {
        val subdir: String = when (hostOs) {
            OS.Linux -> ".cache"
            OS.Windows -> return getFilesDir().resolve("cache")
            else -> throw NotImplementedError(hostOs.name)
        }
        return getHomeDir().resolve(subdir).resolve(app_name.lowercase())
    }

    private fun getTempDir(): File {
        val dir: File = when (hostOs) {
            OS.Linux -> File("/tmp")
            OS.Windows -> getHomeDir().resolve("AppData/Local/Temp")
            else -> throw NotImplementedError(hostOs.name)
        }
        return dir.resolve(app_name.lowercase())
    }

    actual fun isAppInForeground(): Boolean = true // TODO

    actual fun getLightColorScheme(): ColorScheme = lightColorScheme()

    actual fun getDarkColorScheme(): ColorScheme = darkColorScheme()

    actual fun canOpenUrl(): Boolean = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)

    actual fun openUrl(url: String) {
        check(canOpenUrl())
        Desktop.getDesktop().browse(URI(url))
    }

    actual fun canSendNotifications(): Boolean = true

    actual fun sendNotification(title: String, body: String) {
        val icon_path: String = getIconFile()?.absolutePath ?: ""
        Toast.toast(ToastType.INFO, icon_path, title, body)
    }

    actual fun sendNotification(throwable: Throwable) {
        throwable.printStackTrace()
        TODO(throwable.toString())
    }

    actual fun sendToast(text: String, long: Boolean) {
        sendNotification(app_name, text)
    }

    actual fun openFileInput(name: String): FileInputStream =
        getFilesDir().resolve(name).inputStream()

    actual fun openFileOutput(name: String, append: Boolean): FileOutputStream {
        val path: File = getFilesDir().resolve(name)
        path.createNewFile()
        return path.outputStream()
    }

    actual fun deleteFile(name: String): Boolean {
        val file: File = getFilesDir().resolve(name)
        return file.delete()
    }

    private fun getResourceDir(): File = File("/assets")

    actual fun openResourceFile(path: String): InputStream {
        val resource_path: String = getResourceDir().resolve(path).path.replace('\\', '/')

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
        val resource_path: String = getResourceDir().resolve(path).path
        val bytes: ByteArray = resource_class.getResourceAsStream(resource_path)!!.use { stream ->
            stream.readBytes()
        }
        return Font(path, bytes)
    }

    actual fun promptUserForDirectory(persist: Boolean, callback: (uri: String?) -> Unit) {
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

    actual fun promptUserForFile(mime_types: Set<String>, persist: Boolean, callback: (uri: String?) -> Unit) {
        file_chooser.chooseFile(
            getFileChooserConfiguration(),
            createFileChooserCallback(callback)
        )
    }

    actual fun promptUserForFileCreation(mime_type: String, filename_suggestion: String?, persist: Boolean, callback: (uri: String?) -> Unit) {
        val configuration: NativeFileChooserConfiguration = getFileChooserConfiguration()
        configuration.intent = NativeFileChooserIntent.SAVE
        configuration.mimeFilter = mime_type
        file_chooser.chooseFile(configuration, createFileChooserCallback(callback))
    }

    actual fun getUserDirectoryFile(uri: String): PlatformFile {
        return PlatformFile(File(uri))
    }

    // Unsupported and/or too much work
    actual fun isConnectionMetered(): Boolean = false
    actual fun setNavigationBarColour(colour: Color?) {}
    actual fun isDisplayingAboveNavigationBar(): Boolean = false
    actual fun vibrate(duration: Double) {}
    actual fun setStatusBarColour(colour: Color?) {}
    actual fun canShare(): Boolean = false
    actual fun shareText(text: String, title: String?): Unit = throw NotImplementedError()

    @Suppress("NewApi")
    private fun getIconFile(): File? {
        val slash_index: Int = icon_resource_path.lastIndexOf('/')
        val file_name: String = if (slash_index == -1) icon_resource_path else icon_resource_path.substring(slash_index + 1)

        val file: File = getTempDir().resolve(file_name)
        if (!file.isFile) {
            file.parentFile.mkdirs()
            openResourceFile(icon_resource_path).use { icon ->
                Files.copy(icon, Path.of(file.toURI()))
            }
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

actual class PlatformFile(val file: File) {
    actual val uri: String
        get() = file.toURI().path
    actual val name: String
        get() = file.name
    actual val path: String
        get() = file.path
    actual val absolute_path: String
        get() = file.absolutePath
    actual val parent_file: PlatformFile
        get() = PlatformFile(file.parentFile)

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
        if (!exists) {
            return true
        }
        return file.delete()
    }

    actual fun createFile(): Boolean {
        try {
            val parent: File = file.parentFile
            if (!parent.exists()) {
                parent.mkdirs()
            }

            return file.createNewFile()
        }
        catch (e: Throwable) {
            throw IOException("Could not create file $file", e)
        }
    }

    actual fun mkdirs(): Boolean {
        if (file.isDirectory) {
            return true
        }
        return file.mkdirs()
    }

    actual fun renameTo(new_name: String): PlatformFile {
        val dest: PlatformFile = getSibling(new_name)
        check(file.renameTo(dest.file)) { "From $name to $new_name ($absolute_path)" }
        return dest
    }

    @Suppress("NewApi")
    actual fun moveTo(destination: PlatformFile) {
        destination.file.parentFile.mkdirs()
        Files.move(file.toPath(), destination.file.toPath())
    }

    actual fun moveDirContentTo(destination: PlatformFile): Result<PlatformFile> {
        TODO()
    }

    actual companion object {
        actual fun fromFile(
            file: File,
            context: PlatformContext
        ): PlatformFile {
            return PlatformFile(file)
        }
    }

    actual fun matches(other: PlatformFile): Boolean {
        return file == other.file
    }

    override fun toString(): String =
        "PlatformFile(file=${file.absolutePath})"
}
