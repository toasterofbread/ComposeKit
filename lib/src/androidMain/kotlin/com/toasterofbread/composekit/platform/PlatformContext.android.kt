package com.toasterofbread.composekit.platform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsControllerCompat
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.callback.FileCallback
import com.anggrayudi.storage.callback.FolderCallback
import com.anggrayudi.storage.file.*
import com.anggrayudi.storage.media.MediaFile
import com.toasterofbread.composekit.utils.common.isDark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.*
import java.util.*

private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "default_channel"
private const val ERROR_NOTIFICATION_CHANNEL_ID = "download_error_channel"

class ApplicationContext(private val activity: ComponentActivity) {
    private val permission_callbacks: MutableList<(Boolean) -> Unit> = mutableListOf()

    private val permission_launcher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            synchronized(permission_callbacks) {
                for (callback in permission_callbacks) {
                    callback(granted)
                }
                permission_callbacks.clear()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotficationPermission(callback: (granted: Boolean) -> Unit) {
        synchronized(permission_callbacks) {
            permission_callbacks.add(callback)
            if (permission_callbacks.size == 1) {
                permission_launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun takePersistentUriPermission(uri: Uri) {
        val content_resolver: ContentResolver = activity.applicationContext.contentResolver
        val flags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        content_resolver.takePersistableUriPermission(uri, flags)
    }

    fun <I, O> launchFileContract(contract: ActivityResultContract<I, O>, input: I, persist_uri: Boolean = false, callback: (output: O?) -> Unit) {
        val key: String = UUID.randomUUID().toString()

        var launcher: ActivityResultLauncher<I>? = null
        launcher = activity.activityResultRegistry.register(key, contract) { result ->
            if (persist_uri && result is Uri) {
                takePersistentUriPermission(result)
            }
            callback(result)
            launcher?.unregister()
        }

        launcher.launch(input)
    }
}

private val Uri.clean_path: String
    get() = path!!.split(':').last()

private val Uri.split_path: List<String>
    get() = clean_path.split('/').filter { it.isNotBlank() }

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
        get() = file?.exists() == true
    actual val is_directory: Boolean
        get() = file?.isDirectory == true
    actual val is_file: Boolean
        get() = file?.isFile == true

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

    actual fun inputStream(): InputStream =
        context.contentResolver.openInputStream(file!!.uri)!!

    actual fun outputStream(append: Boolean): OutputStream {
        if (!is_file) {
            check(createFile()) { "Could not create file for writing $this" }
        }
        return context.contentResolver.openOutputStream(file!!.uri, if (append) "wa" else "wt")!!
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
        if (file == null) {
            return true
        }
        return file!!.delete()
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
            val new_file: DocumentFile =
                parent_docfile!!.createFile("application/octet-stream", filename) ?: return false

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
            parent_docfile = parent_docfile!!.makeFolder(context, part) ?: return false
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
                            }
                            else {
                                result_channel.send(Result.failure(
                                    IOException("Moving $this to $destination failed (${errorCode.name})")
                                ))
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

    actual companion object {
        actual fun fromFile(file: File, context: PlatformContext): PlatformFile =
            PlatformFile(
                file.toUri(),
                DocumentFile.fromFile(file),
                null,
                context.ctx
            )
    }
}

actual open class PlatformContext(
    private val context: Context,
    private val coroutine_scope: CoroutineScope,
    val application_context: ApplicationContext? = null,
) {
    val ctx: Context get() = context

    actual fun getFilesDir(): File = ctx.filesDir
    actual fun getCacheDir(): File = ctx.cacheDir

    actual fun promptUserForDirectory(persist: Boolean, callback: (uri: String?) -> Unit) {
        check(application_context != null)
        application_context.launchFileContract(
            ActivityResultContracts.OpenDocumentTree(),
            null,
            persist
        ) { uri ->
            callback(uri?.toString())
        }
    }

    actual fun promptUserForFile(mime_types: Set<String>, persist: Boolean, callback: (uri: String?) -> Unit) {
        check(application_context != null)
        application_context.launchFileContract(
            ActivityResultContracts.OpenDocument(),
            mime_types.toTypedArray(),
            persist
        ) { uri ->
            callback(uri?.toString())
        }
    }

    actual fun promptUserForFileCreation(mime_type: String, filename_suggestion: String?, persist: Boolean, callback: (uri: String?) -> Unit) {
        check(application_context != null)
        application_context.launchFileContract(
            ActivityResultContracts.CreateDocument(mime_type),
            filename_suggestion ?: "",
            persist
        ) { uri ->
            callback(uri?.toString())
        }
    }

    actual fun getUserDirectoryFile(uri: String): PlatformFile? {
        val document_uri: Uri = Uri.parse(uri)
        val file: DocumentFile = DocumentFileCompat.fromUri(ctx, document_uri)!!

        try {
            return PlatformFile(document_uri, file, null, ctx)
        }
        catch (_: AssertionError) {
            return null
        }
    }

    actual fun isAppInForeground(): Boolean = ctx.isAppInForeground()

    actual fun setStatusBarColour(colour: Color?) {
        val window: Window = ctx.findWindow() ?: return

        val dark_icons: Boolean
        val bar_colour: Color

        if (colour == null || colour.isUnspecified || colour == Color.Transparent) {
            dark_icons = false
            bar_colour = Color.Black
        }
        else {
            dark_icons = !colour.isDark()
            bar_colour = colour
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (dark_icons) APPEARANCE_LIGHT_STATUS_BARS else 0,
                APPEARANCE_LIGHT_STATUS_BARS
            )
        }
        else {
            window.decorView.apply {
                if (dark_icons) {
                    systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                else {
                    systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        }

        window.statusBarColor = bar_colour.toArgb()
    }

    actual fun setNavigationBarColour(colour: Color?) {
        val window: Window = ctx.findWindow() ?: return
        window.navigationBarColor = (colour ?: Color.Transparent).toArgb()

        val window_insets_controller: WindowInsetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        window_insets_controller.isAppearanceLightNavigationBars = colour?.isDark()?.not() ?: false
    }

    @SuppressLint("DiscouragedApi")
    actual fun isDisplayingAboveNavigationBar(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return true
        }

        val resources: Resources = context.resources

        val resource_id: Int = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        if (resource_id > 0) {
            return resources.getInteger(resource_id) != 2
        }

        return false
    }

    actual fun getLightColorScheme(): ColorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicLightColorScheme(ctx)
        else lightColorScheme()
    actual fun getDarkColorScheme(): ColorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(ctx)
        else darkColorScheme()

    actual fun canShare(): Boolean = true
    actual fun shareText(text: String, title: String?) {
        val share_intent: Intent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"

                putExtra(Intent.EXTRA_TEXT, text)

                if (title != null) {
                    putExtra(Intent.EXTRA_TITLE, title)
                }
            },
            title
        )

        ctx.startActivity(share_intent)
    }

    actual fun canOpenUrl(): Boolean {
        val open_intent: Intent = Intent(Intent.ACTION_VIEW)
        return open_intent.resolveActivity(ctx.packageManager) != null
    }
    actual fun openUrl(url: String) {
        val open_intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        checkNotNull(open_intent.resolveActivity(ctx.packageManager))
        ctx.startActivity(open_intent)
    }

    actual fun sendToast(text: String, long: Boolean) {
        ctx.sendToast(text, long)
    }

    actual fun vibrate(duration: Double) {
        val vibrator: Vibrator = (ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    (duration * 1000.0).toLong(),
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
        else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(
                (duration * 1000.0).toLong()
            )
        }
    }

    actual fun deleteFile(name: String): Boolean = ctx.deleteFile(name)
    actual fun openFileInput(name: String): FileInputStream = ctx.openFileInput(name)
    actual fun openFileOutput(name: String, append: Boolean): FileOutputStream = ctx.openFileOutput(name, if (append) MODE_APPEND else MODE_PRIVATE)

    actual fun openResourceFile(path: String): InputStream = ctx.resources.assets.open(path)
    actual fun listResourceFiles(path: String): List<String>? = ctx.resources.assets.list(path)?.toList()

    actual fun canSendNotifications(): Boolean = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
    @SuppressLint("MissingPermission")
    actual fun sendNotification(title: String, body: String) {
        if (canSendNotifications()) {
            val notification = NotificationCompat.Builder(context, getDefaultNotificationChannel(ctx))
                .setContentTitle(title)
                .setContentText(body)
                .build()

            NotificationManagerCompat.from(ctx).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        }
    }

    @SuppressLint("MissingPermission")
    actual fun sendNotification(throwable: Throwable) {
        RuntimeException(throwable).printStackTrace()
        if (canSendNotifications()) {
            NotificationManagerCompat.from(ctx).notify(
                System.currentTimeMillis().toInt(),
                throwable.createNotification(ctx, getErrorNotificationChannel(ctx))
            )
        }
    }

    actual fun isConnectionMetered(): Boolean = ctx.isConnectionMetered()
}

private fun Context.findWindow(): Window? {
    var context: Context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context.window
        context = context.baseContext
    }
    return null
}

private fun getDefaultNotificationChannel(context: Context): String {
    val channel: NotificationChannelCompat = NotificationChannelCompat.Builder(
        DEFAULT_NOTIFICATION_CHANNEL_ID,
        NotificationManagerCompat.IMPORTANCE_DEFAULT
    ).build()

    NotificationManagerCompat.from(context).createNotificationChannel(channel)
    return DEFAULT_NOTIFICATION_CHANNEL_ID
}

private fun getErrorNotificationChannel(context: Context): String {
    val channel: NotificationChannelCompat =
        NotificationChannelCompat.Builder(
            ERROR_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
        .setName("Error")
        .build()

    NotificationManagerCompat.from(context).createNotificationChannel(channel)
    return ERROR_NOTIFICATION_CHANNEL_ID
}

fun Throwable.createNotification(context: Context, notification_channel: String): Notification {
    return NotificationCompat.Builder(context, notification_channel)
        .setSmallIcon(android.R.drawable.stat_notify_error)
        .setContentTitle(this::class.simpleName)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText("$message\nStack trace:\n${stackTraceToString()}"))
        .addAction(
            NotificationCompat.Action.Builder(
                IconCompat.createWithResource(context, android.R.drawable.ic_menu_share),
                "Share",
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent.createChooser(Intent().also { share ->
                        share.action = Intent.ACTION_SEND
                        share.putExtra(Intent.EXTRA_TITLE, this::class.simpleName)
                        share.putExtra(Intent.EXTRA_TITLE, this::class.simpleName)
                        share.putExtra(Intent.EXTRA_TEXT, stackTraceToString())
                        share.type = "text/plain"
                    }, null),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                )
            ).build()
        )
        .build()
}

fun Context.sendToast(text: String, long: Boolean = false) {
    try {
        Toast.makeText(this, text, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
    catch (_: NullPointerException) {
        Looper.prepare()
        Toast.makeText(this, text, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}

fun Context.isAppInForeground(): Boolean {
    val activity_manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val running_processes: List<ActivityManager.RunningAppProcessInfo> = activity_manager.runningAppProcesses ?: return false
    for (process in running_processes) {
        if (
            process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            && process.processName.equals(packageName)
        ) {
            return true
        }
    }
    return false
}

fun Context.isConnectionMetered(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return manager.isActiveNetworkMetered
}
