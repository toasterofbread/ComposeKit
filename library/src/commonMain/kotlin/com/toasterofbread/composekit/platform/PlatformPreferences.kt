package dev.toastbits.composekit.platform

import kotlinx.serialization.KSerializer

interface PlatformPreferences {
    fun addListener(listener: PlatformPreferencesListener): PlatformPreferencesListener
    fun removeListener(listener: PlatformPreferencesListener)

    fun getString(key: String, default_value: String?): String?
    fun getStringSet(key: String, defValues: Set<String>?): Set<String>?
    fun getInt(key: String, default_value: Int?): Int?
    fun getLong(key: String, default_value: Long?): Long?
    fun getFloat(key: String, default_value: Float?): Float?
    fun getBoolean(key: String, default_value: Boolean?): Boolean?
    fun <T> getSerialisable(key: String, default_value: T, serialiser: KSerializer<T>): T
    operator fun contains(key: String): Boolean

    fun edit(action: Editor.() -> Unit)
    interface Editor {
        fun putString(key: String, value: String): Editor
        fun putStringSet(key: String, values: Set<String>): Editor
        fun putInt(key: String, value: Int): Editor
        fun putLong(key: String, value: Long): Editor
        fun putFloat(key: String, value: Float): Editor
        fun putBoolean(key: String, value: Boolean): Editor
        fun <T> putSerialisable(key: String, value: T, serialiser: KSerializer<T>): Editor
        fun remove(key: String): Editor
        fun clear(): Editor
    }
}

expect fun interface PlatformPreferencesListener {
    fun onChanged(prefs: PlatformPreferences, key: String)
}

expect class PlatformPreferencesImpl: PlatformPreferences {
    override fun addListener(listener: PlatformPreferencesListener): PlatformPreferencesListener
    override fun removeListener(listener: PlatformPreferencesListener)

    override fun getString(key: String, default_value: String?): String?
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>?
    override fun getInt(key: String, default_value: Int?): Int?
    override fun getLong(key: String, default_value: Long?): Long?
    override fun getFloat(key: String, default_value: Float?): Float?
    override fun getBoolean(key: String, default_value: Boolean?): Boolean?
    override fun <T> getSerialisable(key: String, default_value: T, serialiser: KSerializer<T>): T
    override operator fun contains(key: String): Boolean

    override fun edit(action: PlatformPreferences.Editor.() -> Unit)

    open class EditorImpl: PlatformPreferences.Editor {
        override fun putString(key: String, value: String): PlatformPreferences.Editor
        override fun putStringSet(key: String, values: Set<String>): PlatformPreferences.Editor
        override fun putInt(key: String, value: Int): PlatformPreferences.Editor
        override fun putLong(key: String, value: Long): PlatformPreferences.Editor
        override fun putFloat(key: String, value: Float): PlatformPreferences.Editor
        override fun putBoolean(key: String, value: Boolean): PlatformPreferences.Editor
        override fun <T> putSerialisable(key: String, value: T, serialiser: KSerializer<T>): PlatformPreferences.Editor
        override fun remove(key: String): PlatformPreferences.Editor
        override fun clear(): PlatformPreferences.Editor
    }
}
