package dev.toastbits.composekit.platform

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import dev.toastbits.composekit.utils.common.isDark
import kotlinx.coroutines.CoroutineScope

private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "default_channel"
private const val ERROR_NOTIFICATION_CHANNEL_ID = "download_error_channel"

actual open class PlatformContextImpl(
    private val context: Context,
    actual override val coroutine_scope: CoroutineScope,
    override val application_context: ApplicationContext? = null
): PlatformContext {
    override val ctx: Context get() = context

    actual override fun getFilesDir(): PlatformFile? = PlatformFile.fromFile(ctx.filesDir, this)
    actual override fun getCacheDir(): PlatformFile? = PlatformFile.fromFile(ctx.cacheDir, this)

    actual override fun promptUserForDirectory(persist: Boolean, callback: (uri: String?) -> Unit) {
        checkNotNull(application_context)
        application_context!!.launchFileContract(
            ActivityResultContracts.OpenDocumentTree(),
            null,
            persist
        ) { uri ->
            callback(uri?.toString())
        }
    }

    actual override fun promptUserForFile(mime_types: Set<String>, persist: Boolean, callback: (uri: String?) -> Unit) {
        checkNotNull(application_context)
        application_context!!.launchFileContract(
            ActivityResultContracts.OpenDocument(),
            mime_types.toTypedArray(),
            persist
        ) { uri ->
            callback(uri?.toString())
        }
    }

    actual override fun promptUserForFileCreation(mime_type: String, filename_suggestion: String?, persist: Boolean, callback: (uri: String?) -> Unit) {
        checkNotNull(application_context)
        application_context!!.launchFileContract(
            ActivityResultContracts.CreateDocument(mime_type),
            filename_suggestion ?: "",
            persist
        ) { uri ->
            callback(uri?.toString())
        }
    }

    actual override fun getUserDirectoryFile(uri: String): PlatformFile? {
        val document_uri: Uri = Uri.parse(uri)
        val file: DocumentFile = DocumentFileCompat.fromUri(ctx, document_uri)!!

        try {
            return PlatformFile(document_uri, file, null, ctx)
        }
        catch (_: AssertionError) {
            return null
        }
    }

    actual override fun isAppInForeground(): Boolean = ctx.isAppInForeground()

    actual override fun setStatusBarColour(colour: Color?) {
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
                if (dark_icons) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
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

    actual override fun setNavigationBarColour(colour: Color?) {
        val window: Window = ctx.findWindow() ?: return
        window.navigationBarColor = (colour ?: Color.Transparent).toArgb()

        val window_insets_controller: WindowInsetsControllerCompat =
            WindowInsetsControllerCompat(window, window.decorView)
        window_insets_controller.isAppearanceLightNavigationBars = colour?.isDark()?.not() ?: false
    }

    @SuppressLint("DiscouragedApi")
    actual override fun isDisplayingAboveNavigationBar(): Boolean {
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

    actual override fun getLightColorScheme(): ColorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicLightColorScheme(ctx)
        else lightColorScheme()
    actual override fun getDarkColorScheme(): ColorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(ctx)
        else darkColorScheme()

    actual override fun canShare(): Boolean = true
    actual override fun shareText(text: String, title: String?) {
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

    actual override fun canOpenUrl(): Boolean {
        val open_intent: Intent = Intent(Intent.ACTION_VIEW)
        return open_intent.resolveActivity(ctx.packageManager) != null
    }
    actual override fun openUrl(url: String) {
        val open_intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        checkNotNull(open_intent.resolveActivity(ctx.packageManager))
        ctx.startActivity(open_intent)
    }

    actual override fun canCopyText(): Boolean = true
    actual override fun copyText(text: String) {
        val clipboard_manager: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard_manager.setPrimaryClip(ClipData.newPlainText("", text))
    }

    actual override fun sendToast(text: String, long: Boolean) {
        application_context?.apply {
            activity.runOnUiThread {
                ctx.sendToast(text, long)
            }
            return
        }

        ctx.sendToast(text, long)
    }

    actual override fun vibrate(duration: Double) {
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

    actual override fun canSendNotifications(): Boolean = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
    @SuppressLint("MissingPermission")
    actual override fun sendNotification(title: String, body: String) {
        if (canSendNotifications()) {
            val notification = NotificationCompat.Builder(
                context,
                getDefaultNotificationChannel(ctx)
            )
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
    actual override fun sendNotification(throwable: Throwable) {
        RuntimeException(throwable).printStackTrace()
        if (canSendNotifications()) {
            NotificationManagerCompat.from(ctx).notify(
                System.currentTimeMillis().toInt(),
                throwable.createNotification(ctx, getErrorNotificationChannel(ctx))
            )
        }
    }

    actual override fun isConnectionMetered(): Boolean = ctx.isConnectionMetered()
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
    catch (e: Throwable) {
        RuntimeException("Sending toast '$text' (long=$long) failed", e).printStackTrace()
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
