package dev.toastbits.composekit.platform

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

actual fun interface PlatformPreferencesListener {
    actual fun onChanged(key: String)
}

actual class PlatformPreferencesImpl(actual override val json: Json): PlatformPreferences {
    actual override fun addListener(listener: PlatformPreferencesListener): PlatformPreferencesListener {
        return listener
    }

    actual override fun removeListener(listener: PlatformPreferencesListener) {}

    actual override fun getString(
        key: String,
        default_value: String?
    ): String? = default_value

    actual override fun getStringSet(
        key: String,
        default_values: Set<String>?
    ): Set<String>? = default_values

    actual override fun getInt(key: String, default_value: Int?): Int? = default_value

    actual override fun getLong(key: String, default_value: Long?): Long? = default_value

    actual override fun getFloat(key: String, default_value: Float?): Float? = default_value

    actual override fun getBoolean(
        key: String,
        default_value: Boolean?
    ): Boolean? = default_value

    actual override fun <T> getSerialisable(
        key: String,
        default_value: T,
        serialiser: KSerializer<T>,
        json: Json
    ): T = default_value

    actual override fun contains(key: String): Boolean = false

    actual override fun edit(action: PlatformPreferences.Editor.() -> Unit) {}

    actual inner class EditorImpl : PlatformPreferences.Editor {
        actual override val json: Json get() = this@PlatformPreferencesImpl.json

        actual override fun putString(
            key: String,
            value: String
        ): PlatformPreferences.Editor = this

        actual override fun putStringSet(
            key: String,
            values: Set<String>
        ): PlatformPreferences.Editor = this

        actual override fun putInt(
            key: String,
            value: Int
        ): PlatformPreferences.Editor = this

        actual override fun putLong(
            key: String,
            value: Long
        ): PlatformPreferences.Editor = this

        actual override fun putFloat(
            key: String,
            value: Float
        ): PlatformPreferences.Editor = this

        actual override fun putBoolean(
            key: String,
            value: Boolean
        ): PlatformPreferences.Editor = this

        actual override fun <T> putSerialisable(
            key: String,
            value: T,
            serialiser: KSerializer<T>,
            json: Json
        ): PlatformPreferences.Editor = this

        actual override fun remove(key: String): PlatformPreferences.Editor = this

        actual override fun clear(): PlatformPreferences.Editor = this
    }
}
