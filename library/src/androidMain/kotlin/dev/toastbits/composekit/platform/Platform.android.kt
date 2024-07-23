package dev.toastbits.composekit.platform

import android.os.Build
import java.lang.reflect.Field

actual fun getPlatform(): Platform =
    Platform.ANDROID

actual fun getPlatformForbiddenFilenameCharacters(): String = "/"

actual fun getPlatformOSName(): String {
    val fields: Array<Field> = Build.VERSION_CODES::class.java.fields
    val version: String =
        fields.firstOrNull { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }?.name?.lowercase()?.replaceFirstChar { it.uppercase() }
        ?: ""
    return "Android $version".trim()

}

actual fun getPlatformHostName(): String = Build.MODEL
