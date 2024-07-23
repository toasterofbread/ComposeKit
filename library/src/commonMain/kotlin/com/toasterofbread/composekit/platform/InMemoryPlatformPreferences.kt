package dev.toastbits.composekit.platform

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.KSerializer

open class InMemoryPlatformPreferences(): PlatformPreferences {
    protected open val data: MutableMap<String, JsonElement> = mutableMapOf()
    private val listeners: MutableList<PlatformPreferencesListener> = mutableListOf()

    private fun onKeyChanged(key: String) {
        for (listener in listeners) {
            listener.onChanged(this, key)
        }
    }

    override fun addListener(listener: PlatformPreferencesListener): PlatformPreferencesListener {
        listeners.add(listener)
        return listener
    }

    override fun removeListener(listener: PlatformPreferencesListener) {
        listeners.remove(listener)
    }

    override fun getString(key: String, default_value: String?): String? =
        data.get(key)?.jsonPrimitive?.takeIf { it.isString }?.content ?: default_value

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, default_values: Set<String>?): Set<String>? =
        data.get(key)?.jsonArray?.map { it.jsonPrimitive.content }?.toSet() ?: default_values

    override fun getInt(key: String, default_value: Int?): Int? =
        data.get(key)?.jsonPrimitive?.int ?: default_value

    override fun getLong(key: String, default_value: Long?): Long? =
        data.get(key)?.jsonPrimitive?.long ?: default_value

    override fun getFloat(key: String, default_value: Float?): Float? =
        data.get(key)?.jsonPrimitive?.float ?: default_value

    override fun getBoolean(key: String, default_value: Boolean?): Boolean? =
        data.get(key)?.jsonPrimitive?.boolean ?: default_value

    override fun <T> getSerialisable(key: String, default_value: T, serialiser: KSerializer<T>): T {
        val json: Json =
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }

        val value: JsonElement = data.get(key) ?: return default_value
        if (value is JsonPrimitive) {
            return json.decodeFromString(serialiser, value.content)
        }

        return json.decodeFromJsonElement(serialiser, value)
    }

    override operator fun contains(key: String): Boolean =
        data.containsKey(key)

    override fun edit(action: PlatformPreferences.Editor.() -> Unit) {
        val changed: MutableSet<String> = mutableSetOf()
        val editor: EditorImpl = EditorImpl(data, changed)
        action(editor)

        for (key in changed) {
            onKeyChanged(key)
        }
    }

    open class EditorImpl(private val data: MutableMap<String, JsonElement>, private val changed: MutableSet<String>): PlatformPreferences.Editor {
        override fun putString(key: String, value: String): PlatformPreferences.Editor {
            data[key] = Json.encodeToJsonElement(value)
            changed.add(key)
            return this
        }

        override fun putStringSet(
            key: String,
            values: Set<String>,
        ): PlatformPreferences.Editor {
            data[key] = Json.encodeToJsonElement(values)
            changed.add(key)
            return this
        }

        override fun putInt(key: String, value: Int): PlatformPreferences.Editor {
            data[key] = Json.encodeToJsonElement(value)
            changed.add(key)
            return this
        }

        override fun putLong(key: String, value: Long): PlatformPreferences.Editor {
            data[key] = Json.encodeToJsonElement(value)
            changed.add(key)
            return this
        }

        override fun putFloat(key: String, value: Float): PlatformPreferences.Editor {
            data[key] = Json.encodeToJsonElement(value)
            changed.add(key)
            return this
        }

        override fun putBoolean(key: String, value: Boolean): PlatformPreferences.Editor {
            data[key] = Json.encodeToJsonElement(value)
            changed.add(key)
            return this
        }

        override fun <T> putSerialisable(key: String, value: T, serialiser: KSerializer<T>): PlatformPreferences.Editor {
            data[key] = Json.encodeToJsonElement(serialiser, value)
            changed.add(key)
            return this
        }

        override fun remove(key: String): PlatformPreferences.Editor {
            data.remove(key)
            changed.add(key)
            return this
        }

        override fun clear(): PlatformPreferences.Editor {
            changed.addAll(data.keys)
            data.clear()
            return this
        }
    }
}
