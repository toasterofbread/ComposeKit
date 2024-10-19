package dev.toastbits.composekit.platform

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

actual class PlatformPreferencesImpl private constructor(private val file: File, json: Json): PlatformPreferencesJson(
    PlatformFile(file), json
), PlatformPreferences {
    companion object {
        private var instance: PlatformPreferencesImpl? = null

        fun getInstance(file: File, json: Json): PlatformPreferences {
            if (instance == null) {
                instance = PlatformPreferencesImpl(file, json)
            }
            check(instance!!.file == file)
            return instance!!
        }
    }

    actual inner class EditorImpl(private val data: MutableMap<String, Any>, private val changed: MutableSet<String>): PlatformPreferences.Editor {
        actual override val json: Json get() = this@PlatformPreferencesImpl.json

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

        actual override fun <T> putSerialisable(key: String, value: T, serialiser: KSerializer<T>, json: Json): PlatformPreferences.Editor {
            data[key] = json.encodeToJsonElement(serialiser, value)
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

actual fun interface PlatformPreferencesListener {
    actual fun onChanged(key: String)
}
