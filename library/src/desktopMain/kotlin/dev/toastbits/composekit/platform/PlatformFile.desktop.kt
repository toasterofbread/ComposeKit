package dev.toastbits.composekit.platform

import okio.Sink
import okio.Source
import okio.sink
import okio.source
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files

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

    actual fun inputStream(): Source {
        return file.inputStream().source()
    }

    actual fun outputStream(append: Boolean): Sink {
        return FileOutputStream(file, append).sink()
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
        if (matches(destination)) {
            return
        }

        destination.file.parentFile.mkdirs()
        Files.move(file.toPath(), destination.file.toPath())
    }

    actual fun moveDirContentTo(destination: PlatformFile): Result<PlatformFile> {
        TODO()
    }

    actual fun matches(other: PlatformFile): Boolean {
        return file == other.file
    }

    override fun toString(): String =
        "PlatformFile(file=${file.absolutePath})"

    actual companion object
}