package com.toasterofbread.composekit.platform

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

actual class PlatformPreferencesImpl private constructor(private val prefs: SharedPreferences): PlatformPreferences {
    companion object {
        private var instance: PlatformPreferences? = null

        fun getInstance(context: Context): PlatformPreferences {
            return getInstance(context.getSharedPreferences("com.toasterofbread.composekit.PREFERENCES", Context.MODE_PRIVATE))
        }
        fun getInstance(prefs: SharedPreferences): PlatformPreferences {
            if (instance == null) {
                instance = PlatformPreferencesImpl(prefs)
            }
            return instance!!
        }
    }

    actual override fun getString(key: String, defValue: String?): String? = prefs.getString(key, defValue)
    actual override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? = prefs.getStringSet(key, defValues)
    actual override fun getInt(key: String, defValue: Int?): Int? {
        if (!prefs.contains(key)) {
            return defValue
        }
        return prefs.getInt(key, 0)
    }
    actual override fun getLong(key: String, defValue: Long?): Long? {
        if (!prefs.contains(key)) {
            return defValue
        }
        return prefs.getLong(key, 0)
    }
    actual override fun getFloat(key: String, defValue: Float?): Float? {
        if (!prefs.contains(key)) {
            return defValue
        }
        return prefs.getFloat(key, 0f)
    }
    actual override fun getBoolean(key: String, defValue: Boolean?): Boolean? {
        if (!prefs.contains(key)) {
            return defValue
        }
        return prefs.getBoolean(key, false)
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

    actual open class EditorImpl(private val upstream: SharedPreferences.Editor): PlatformPreferences.Editor {
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

actual interface PlatformPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
        if (key != null) {
            onChanged(PlatformPreferencesImpl.getInstance(prefs), key)
        }
    }
    actual fun onChanged(prefs: PlatformPreferences, key: String)
}
