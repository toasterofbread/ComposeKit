package dev.toastbits.composekit.platform

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.callback.FileCallback
import com.anggrayudi.storage.callback.FolderCallback
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.child
import com.anggrayudi.storage.file.copyFileTo
import com.anggrayudi.storage.file.findParent
import com.anggrayudi.storage.file.getAbsolutePath
import com.anggrayudi.storage.file.makeFolder
import com.anggrayudi.storage.file.moveFileTo
import com.anggrayudi.storage.file.moveFolderTo
import com.anggrayudi.storage.media.MediaFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okio.Sink
import okio.Source
import okio.sink
import okio.source
import java.io.File
import java.io.IOException

actual class PlatformFile(
    document_uri: Uri,
    private var file: DocumentFile?,
    private var parent_docfile: DocumentFile?,
    private val context: Context
) {
    var document_uri: Uri = document_uri
        private set

    init {
        assert(file != null || parent_docfile != null) {
            "PlatformFile must be created with file or parent_file ($document_uri)"
        }

        if (file != null) {
            assert(file!!.exists()) {
                "File does not exist ($document_uri)"
            }
        }
        if (parent_docfile != null) {
            assert(parent_docfile!!.isDirectory) {
                "Parent file is not a directory (${parent_docfile!!.getAbsolutePath(context)} | $document_uri)"
            }
        }
    }

    actual val uri: String
        get() = document_uri.toString()
    actual val name: String
        get() = file?.name ?: document_uri.path!!.split('/').last()
    actual val path: String
        get() = document_uri.clean_path
    actual val absolute_path: String
        get() = path
    actual val parent_file: PlatformFile
        get() {
            val parent: DocumentFile = parent_docfile ?: file!!.parentFile!!
            return PlatformFile(parent.uri, parent, null, context)
        }

    actual val exists: Boolean
        get() {
            if (file?.exists() == true) {
                return true
            }

            try {
                return File(absolute_path).exists()
            }
            catch (_: Throwable) {
                return false
            }
        }
    actual val is_directory: Boolean
        get() {
            if (file?.isDirectory == true) {
                return true
            }

            try {
                return File(absolute_path).isDirectory()
            }
            catch (_: Throwable) {
                return false
            }
        }
    actual val is_file: Boolean
        get() {
            if (file?.isFile == true) {
                return true
            }

            try {
                return File(absolute_path).isFile()
            }
            catch (_: Throwable) {
                return false
            }
        }

    actual fun getRelativePath(relative_to: PlatformFile): String {
        require(relative_to.is_directory)

        val relative_split: List<String> = relative_to.absolute_path.split('/')
        val path_split: MutableList<String> = absolute_path.split('/').toMutableList()

        for (part in relative_split.withIndex()) {
            if (part.value == path_split.firstOrNull()) {
                path_split.removeAt(0)
            }
            else {
                path_split.add(
                    0,
                    "../".repeat(relative_split.size - part.index).dropLast(1)
                )
            }
        }

        return path_split.joinToString("/")
    }

    actual fun inputStream(): Source {
        try {
            return context.contentResolver.openInputStream(file!!.uri)!!.source()
        }
        catch (e: Throwable) {
            try {
                return File(absolute_path).inputStream().source()
            }
            catch (_: Throwable) {}

            throw IOException("Could not open input stream for file '$this'", e)
        }
    }

    actual fun outputStream(append: Boolean): Sink {
        if (!is_file) {
            createFile()
        }

        try {
            return context.contentResolver.openOutputStream(file!!.uri, if (append) "wa" else "wt")!!.sink()
        }
        catch (e: Throwable) {
            try {
                return File(absolute_path).outputStream().sink()
            }
            catch (_: Throwable) {}

            throw IOException("Could not open output stream for file '$this'", e)
        }
    }

    actual fun listFiles(): List<PlatformFile>? =
        file?.listFiles()?.map {
            PlatformFile(it.uri, it, null, context)
        }

    actual fun resolve(relative_path: String): PlatformFile {
        val uri: Uri = document_uri.buildUpon().appendPath(relative_path).build()

        if (file != null) {
            var existing_file: DocumentFile = file!!

            for (part in relative_path.split('/')) {
                val child: DocumentFile? = existing_file.findFile(part)
                if (child == null) {
                    return PlatformFile(uri, null, existing_file, context)
                }
                existing_file = child
            }

            return PlatformFile(uri, existing_file, null, context)
        }
        else {
            return PlatformFile(uri, null, parent_docfile, context)
        }
    }

    actual fun getSibling(sibling_name: String): PlatformFile {
        val uri: String = document_uri.toString()
        val last_slash: Int = uri.lastIndexOf('/')
        check(last_slash != -1)

        val sibling_uri: Uri = Uri.parse(uri.substring(0, last_slash + 1) + sibling_name)

        if (file != null) {
            return PlatformFile(sibling_uri, null, file!!.findParent(context, true)!!, context)
        }
        else {
            return PlatformFile(sibling_uri, null, parent_docfile!!, context)
        }
    }

    actual fun delete(): Boolean {
        if (!exists) {
            return true
        }

        if (file?.delete() == true) {
            return true
        }

        try {
            return File(absolute_path).delete()
        }
        catch (_: Throwable) {
            return false
        }
    }

    actual fun createFile(): Boolean {
        if (is_file) {
            return true
        }
        else if (file != null || parent_docfile == null) {
            return false
        }

        val parts: List<String> = document_uri.split_path.drop(parent_docfile!!.uri.split_path.size).dropLast(1)
        for (part in parts) {
            parent_docfile = parent_docfile!!.makeFolder(context, part) ?: return false
        }

        try {
            val filename: String = name
            val new_file: DocumentFile? = parent_docfile!!.createFile("application/octet-stream", filename)

            if (new_file == null) {
                try {
                    val java_file: File = File(absolute_path)
                    val parent: File = java_file.parentFile
                    if (!parent.exists()) {
                        parent.mkdirs()
                    }

                    return java_file.createNewFile()
                }
                catch (_: Throwable) {
                    return false
                }
            }

            if (new_file.name != name) {
                new_file.renameTo(filename)
            }

            document_uri = new_file.uri
            file = new_file
            parent_docfile = null

            return true
        }
        catch (e: Throwable) {
            throw RuntimeException(toString(), e)
        }
    }

    actual fun mkdirs(): Boolean {
        if (file != null) {
            return true
        }

        val parts: List<String> = document_uri.split_path.drop(parent_docfile!!.uri.split_path.size)
        for (part in parts) {
            val new_parent: DocumentFile? = parent_docfile!!.makeFolder(context, part)
            if (new_parent == null) {
                try {
                    return File(absolute_path).mkdirs()
                }
                catch (_: Throwable) {
                    return false
                }
            }

            parent_docfile = new_parent
        }

        file = parent_docfile
        parent_docfile = null

        return true
    }

    actual fun renameTo(new_name: String): PlatformFile {
        file!!.renameTo(new_name)

        val parent: DocumentFile? = file!!.parentFile
        checkNotNull(parent) { file.toString() }

        val new_file: DocumentFile? = parent.child(context, new_name)
        checkNotNull(new_file) { "$parent | $new_name" }

        return PlatformFile(
            new_file.uri,
            new_file,
            null,
            context
        )
    }

    @SuppressLint("NewApi")
    actual fun moveTo(destination: PlatformFile) {
        if (matches(destination)) {
            return
        }

        check(is_file) { "File $this does not exist" }
        check(destination.createFile()) { "Could not create destination file $destination" }

        runBlocking {
            val result_channel: Channel<Result<Unit>> = Channel()

            file!!.moveFileTo(
                context,
                MediaFile(context, destination.document_uri),
                object : FileCallback() {
                    override fun onCompleted(result: Any) {
                        runBlocking {
                            result_channel.send(Result.success(Unit))
                        }
                    }

                    override fun onFailed(errorCode: ErrorCode) {
                        runBlocking {
                            if (errorCode == ErrorCode.UNKNOWN_IO_ERROR) {
                                result_channel.send(Result.success(Unit))
                            } else {
                                result_channel.send(
                                    Result.failure(
                                        IOException("Moving $this to $destination failed (${errorCode.name})")
                                    )
                                )
                            }
                        }
                    }
                }
            )

            result_channel.receive().getOrThrow()
        }
    }

//    actual fun copyTo(destination: PlatformFile) {
//        check(is_file)
//
//        file!!.copyFileTo(
//            context,
//            MediaFile(context, destination.document_uri),
//            object : FileCallback() {
//
//            }
//        )
//
//        DocumentsContract.copyDocument(context.contentResolver, document_uri, destination.document_uri)
//    }

    actual fun moveDirContentTo(destination: PlatformFile): Result<PlatformFile> {
        if (!is_directory) {
            return Result.failure(IllegalStateException("Not a directory"))
        }

        val files: Array<DocumentFile> = file!!.listFiles()
        var error: Throwable? = null

        for (item in files) {
            if (item.isFile) {
                item.copyFileTo(
                    context,
                    MediaFile(context, destination.file!!.child(context, item.name!!)!!.uri),
                    object : FileCallback() {
                        override fun onFailed(errorCode: ErrorCode) {
                            if (error == null) {
                                error = RuntimeException(errorCode.toString())
                            }
                        }
                    }
                )
            }
            else if (item.isDirectory) {
                item.moveFolderTo(
                    context,
                    DocumentFileCompat.fromUri(context, destination.file!!.uri)!!,
                    false,
                    callback = object : FolderCallback() {
                        override fun onFailed(errorCode: ErrorCode) {
                            if (error == null) {
                                error = RuntimeException(errorCode.toString())
                            }
                        }
                    }
                )
            }
            else {
                throw IllegalStateException(item.uri.clean_path)
            }

            if (error != null) {
                return Result.failure(error!!)
            }
        }

        return Result.success(destination)
    }

    actual fun matches(other: PlatformFile): Boolean {
        return document_uri.clean_path == other.document_uri.clean_path
    }

    override fun toString(): String =
        "PlatformFile(uri=${document_uri.clean_path}, file=${file?.uri?.clean_path}, parent_file=${parent_docfile?.uri?.clean_path})"

    actual companion object
}

private val Uri.clean_path: String
    get() = path!!.split(':').last()

private val Uri.split_path: List<String>
    get() = clean_path.split('/').filter { it.isNotBlank() }

actual fun PlatformFile.Companion.fromFile(file: File, context: PlatformContext): PlatformFile =
    PlatformFile(
        file.toUri(),
        DocumentFile.fromFile(file),
        null,
        context.ctx
    )
