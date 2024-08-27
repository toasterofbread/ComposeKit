package dev.toastbits.composekit.platform

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import java.util.UUID

class ApplicationContext(internal val activity: ComponentActivity) {
    private val permission_callbacks: MutableList<(Boolean) -> Unit> = mutableListOf()

    private val permission_launcher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            dev.toastbits.composekit.platform.synchronized(permission_callbacks) {
                for (callback in permission_callbacks) {
                    callback(granted)
                }
                permission_callbacks.clear()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotficationPermission(callback: (granted: Boolean) -> Unit) {
        dev.toastbits.composekit.platform.synchronized(permission_callbacks) {
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

    fun simulateBackPress() {
        activity.onBackPressed()
    }
}
