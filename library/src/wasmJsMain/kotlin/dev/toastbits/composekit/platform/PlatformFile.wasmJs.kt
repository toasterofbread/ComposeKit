package dev.toastbits.composekit.platform

import okio.Sink
import okio.Source

actual class PlatformFile{
    init {
        throw IllegalStateException()
    }

    actual val uri: String
        get() = throw IllegalStateException()
    actual val name: String
        get() = throw IllegalStateException()
    actual val path: String
        get() = throw IllegalStateException()
    actual val absolute_path: String
        get() = throw IllegalStateException()
    actual val parent_file: PlatformFile
        get() = throw IllegalStateException()
    actual val exists: Boolean
        get() = throw IllegalStateException()
    actual val is_directory: Boolean
        get() = throw IllegalStateException()
    actual val is_file: Boolean
        get() = throw IllegalStateException()

    actual fun getRelativePath(relative_to: PlatformFile): String = throw IllegalStateException()

    actual fun inputStream(): Source = throw IllegalStateException()

    actual fun outputStream(append: Boolean): Sink = throw IllegalStateException()

    actual fun listFiles(): List<PlatformFile>? = throw IllegalStateException()

    actual fun resolve(relative_path: String): PlatformFile = throw IllegalStateException()

    actual fun getSibling(sibling_name: String): PlatformFile = throw IllegalStateException()

    actual fun delete(): Boolean = throw IllegalStateException()

    actual fun createFile(): Boolean = throw IllegalStateException()

    actual fun mkdirs(): Boolean = throw IllegalStateException()

    actual fun renameTo(new_name: String): PlatformFile = throw IllegalStateException()

    actual fun moveTo(destination: PlatformFile) { throw IllegalStateException() }

    actual fun moveDirContentTo(destination: PlatformFile): Result<PlatformFile> = throw IllegalStateException()

    actual fun matches(other: PlatformFile): Boolean = throw IllegalStateException()

    actual companion object
}