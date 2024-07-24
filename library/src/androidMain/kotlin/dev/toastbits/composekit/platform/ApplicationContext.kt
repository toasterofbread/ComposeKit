package dev.toastbits.composekit.platform

import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi

interface ApplicationContext {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission(callback: (granted: Boolean) -> Unit)
    fun <I, O> launchFileContract(contract: ActivityResultContract<I, O>, input: I, persist_uri: Boolean = false, callback: (output: O?) -> Unit)
    fun simulateBackPress()
}
