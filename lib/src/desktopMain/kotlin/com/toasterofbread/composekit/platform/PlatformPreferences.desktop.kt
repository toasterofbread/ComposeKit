package com.toasterofbread.composekit.platform

import java.io.File

actual class PlatformPreferencesImpl private constructor(file: File): PlatformPreferencesJson(PlatformFile(file)), PlatformPreferences {
    companion object {
        private var instance: PlatformPreferences? = null

        fun getInstance(getFile: () -> File): PlatformPreferences {
            if (instance == null) {
                instance = PlatformPreferencesImpl(getFile())
            }
            return instance!!
        }
    }

    actual open class EditorImpl(private val data: MutableMap<String, Any>, private val changed: MutableSet<String>): PlatformPreferences.Editor {
        actual override fun putString(key: String, value: String): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        actual override fun putStringSet(
            key: String,
            values: Set<String>,
        ): PlatformPreferences.Editor {
            data[key] = values
            changed.add(key)
            return this
        }

        actual override fun putInt(key: String, value: Int): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        actual override fun putLong(key: String, value: Long): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        actual override fun putFloat(key: String, value: Float): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        actual override fun putBoolean(key: String, value: Boolean): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        actual override fun remove(key: String): PlatformPreferences.Editor {
            data.remove(key)
            return this
        }

        actual override fun clear(): PlatformPreferences.Editor {
            changed.addAll(data.keys)
            data.clear()
            return this
        }
    }
}

actual interface PlatformPreferencesListener {
    actual fun onChanged(prefs: PlatformPreferences, key: String)
}
