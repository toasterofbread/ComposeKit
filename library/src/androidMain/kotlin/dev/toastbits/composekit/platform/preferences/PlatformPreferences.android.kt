package dev.toastbits.composekit.platform.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

actual class PlatformPreferencesImpl private constructor(
    private val prefs: SharedPreferences,
    actual override val json: Json
): PlatformPreferences {
    companion object {
        private var instance: PlatformPreferences? = null

        fun getInstance(context: Context, json: Json): PlatformPreferences {
            return getInstance(context.getSharedPreferences("dev.toastbits.composekit.PREFERENCES", Context.MODE_PRIVATE), json)
        }
        fun getInstance(prefs: SharedPreferences, json: Json): PlatformPreferences {
            if (instance == null) {
                instance = PlatformPreferencesImpl(prefs, json)
            }
            return instance!!
        }
    }

    actual override fun getString(key: String, default_value: String?): String? =
        prefs.getString(key, default_value)

    actual override fun getStringSet(key: String, default_values: Set<String>?): Set<String>? =
        prefs.getStringSet(key, default_values)

    actual override fun getInt(key: String, default_value: Int?): Int? {
        if (!prefs.contains(key)) {
            return default_value
        }
        return prefs.getInt(key, 0)
    }

    actual override fun getLong(key: String, default_value: Long?): Long? {
        if (!prefs.contains(key)) {
            return default_value
        }
        return prefs.getLong(key, 0)
    }

    actual override fun getFloat(key: String, default_value: Float?): Float? {
        if (!prefs.contains(key)) {
            return default_value
        }
        return prefs.getFloat(key, 0f)
    }

    actual override fun getBoolean(key: String, default_value: Boolean?): Boolean? {
        if (!prefs.contains(key)) {
            return default_value
        }
        return prefs.getBoolean(key, false)
    }

    actual override fun <T> getSerialisable(key: String, default_value: T, serialiser: KSerializer<T>, json: Json): T {
        val data: String = prefs.getString(key, null) ?: return default_value
        try {
            return json.decodeFromString(serialiser, data)
        }
        catch (e: Throwable) {
            throw RuntimeException("Deserialising prefs key '$key' with value '$data' failed", e)
        }
    }

    actual override operator fun contains(key: String): Boolean = prefs.contains(key)

    actual override fun addListener(listener: PlatformPreferencesListener): PlatformPreferencesListener {
        prefs.registerOnSharedPreferenceChangeListener(listener as SharedPreferences.OnSharedPreferenceChangeListener)
        return listener
    }

    actual override fun removeListener(listener: PlatformPreferencesListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener as SharedPreferences.OnSharedPreferenceChangeListener)
    }

    actual override fun edit(action: PlatformPreferences.Editor.() -> Unit) {
        prefs.edit {
            action(EditorImpl(this))
        }
    }

    actual inner class EditorImpl(private val upstream: SharedPreferences.Editor): PlatformPreferences.Editor {
        actual override val json: Json get() = this@PlatformPreferencesImpl.json

        actual override fun putString(key: String, value: String): PlatformPreferences.Editor {
            upstream.putString(key, value)
            return this
        }

        actual override fun putStringSet(
            key: String,
            values: Set<String>
        ): PlatformPreferences.Editor {
            upstream.putStringSet(key, values)
            return this
        }

        actual override fun putInt(key: String, value: Int): PlatformPreferences.Editor {
            upstream.putInt(key, value)
            return this
        }

        actual override fun putLong(key: String, value: Long): PlatformPreferences.Editor {
            upstream.putLong(key, value)
            return this
        }

        actual override fun putFloat(key: String, value: Float): PlatformPreferences.Editor {
            upstream.putFloat(key, value)
            return this
        }

        actual override fun putBoolean(key: String, value: Boolean): PlatformPreferences.Editor {
            upstream.putBoolean(key, value)
            return this
        }

        actual override fun <T> putSerialisable(key: String, value: T, serialiser: KSerializer<T>, json: Json): PlatformPreferences.Editor {
            upstream.putString(key, json.encodeToString(serialiser, value))
            return this
        }

        actual override fun remove(key: String): PlatformPreferences.Editor {
            upstream.remove(key)
            return this
        }

        actual override fun clear(): PlatformPreferences.Editor {
            upstream.clear()
            return this
        }
    }
}

actual fun interface PlatformPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        if (key != null) {
            onChanged(key)
        }
    }
    actual fun onChanged(key: String)
}
