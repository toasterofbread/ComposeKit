package com.toasterofbread.composekit.platform

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStream
import java.io.OutputStreamWriter
import java.lang.reflect.Type

open class PlatformPreferencesJson(private val file: PlatformFile): PlatformPreferences {
    private val data: MutableMap<String, Any> by lazy {
        loadData()
    }
    private var listeners: MutableList<PlatformPreferencesListener> = mutableListOf()

    private fun onKeyChanged(key: String) {
        for (listener in listeners) {
            listener.onChanged(this, key)
        }
    }
    
    protected open fun loadData(): MutableMap<String, Any> {
        if (!file.exists) {
            return mutableMapOf()
        }

        return file.inputStream().reader().use { reader ->
            val type: Type = object : TypeToken<Map<String, Any>>() {}.type
            Gson().fromJson(reader, type) ?: mutableMapOf()
        }
    }
    private fun saveData() {
        file.createFile()
        file.outputStream().writer().use { writer ->
            writer.write(Gson().toJson(data))
            writer.flush()
        }
    }

    override fun addListener(listener: PlatformPreferencesListener): PlatformPreferencesListener {
        listeners.add(listener)
        return listener
    }

    override fun removeListener(listener: PlatformPreferencesListener) {
        listeners.remove(listener)
    }

    override fun getString(key: String, defValue: String?): String? =
        data.getOrDefault(key, defValue) as String?

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? =
        (data.getOrDefault(key, defValues) as Iterable<String>).toSet()

    override fun getInt(key: String, defValue: Int?): Int? =
        (data.getOrDefault(key, defValue) as Number?)?.toInt()

    override fun getLong(key: String, defValue: Long?): Long? =
        data.getOrDefault(key, defValue) as Long?

    override fun getFloat(key: String, defValue: Float?): Float? =
        (data.getOrDefault(key, defValue) as Number?)?.toFloat()

    override fun getBoolean(key: String, defValue: Boolean?): Boolean? =
        data.getOrDefault(key, defValue) as Boolean?

    override operator fun contains(key: String): Boolean =
        data.containsKey(key)

    override fun edit(action: PlatformPreferences.Editor.() -> Unit) {
        val changed: MutableSet<String> = mutableSetOf()
        val editor: EditorImpl = EditorImpl(data, changed)
        action(editor)
        saveData()

        for (key in changed) {
            onKeyChanged(key)
        }
    }

    open class EditorImpl(private val data: MutableMap<String, Any>, private val changed: MutableSet<String>): PlatformPreferences.Editor {
        override fun putString(key: String, value: String): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        override fun putStringSet(
            key: String,
            values: Set<String>,
        ): PlatformPreferences.Editor {
            data[key] = values
            changed.add(key)
            return this
        }

        override fun putInt(key: String, value: Int): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        override fun putLong(key: String, value: Long): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        override fun putFloat(key: String, value: Float): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        override fun putBoolean(key: String, value: Boolean): PlatformPreferences.Editor {
            data[key] = value
            changed.add(key)
            return this
        }

        override fun remove(key: String): PlatformPreferences.Editor {
            data.remove(key)
            return this
        }

        override fun clear(): PlatformPreferences.Editor {
            changed.addAll(data.keys)
            data.clear()
            return this
        }
    }
}
