package dev.toastbits.composekit.platform

import okio.Sink
import okio.Source

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