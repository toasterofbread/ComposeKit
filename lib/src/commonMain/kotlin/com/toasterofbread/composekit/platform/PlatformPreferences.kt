package com.toasterofbread.composekit.platform

@Suppress("UNCHECKED_CAST")
fun PlatformPreferences.Editor.putAny(key: String, value: Any?, default: Any): PlatformPreferences.Editor {
    val set_value: Any = value ?: default
    return when (default) {
        is String -> putString(key, set_value as String)
        is Iterable<*> -> putStringSet(key, (set_value as Iterable<String>).toSet())
        is Int -> putInt(key, (set_value as Number).toInt())
        is Long -> putLong(key, (set_value as Number).toLong())
        is Float, is Double -> putFloat(key, (set_value as Number).toFloat())
        is Boolean -> putBoolean(key, set_value as Boolean)
        else -> throw NotImplementedError("Key: $key, value: $set_value (${set_value::class})")
    }
}

interface PlatformPreferences {
    fun addListener(listener: PlatformPreferencesListener): PlatformPreferencesListener
    fun removeListener(listener: PlatformPreferencesListener)

    fun getString(key: String, defValue: String?): String?
    fun getStringSet(key: String, defValues: Set<String>?): Set<String>?
    fun getInt(key: String, defValue: Int?): Int?
    fun getLong(key: String, defValue: Long?): Long?
    fun getFloat(key: String, defValue: Float?): Float?
    fun getBoolean(key: String, defValue: Boolean?): Boolean?
    operator fun contains(key: String): Boolean

    fun edit(action: Editor.() -> Unit)
    interface Editor {
        fun putString(key: String, value: String): Editor
        fun putStringSet(key: String, values: Set<String>): Editor
        fun putInt(key: String, value: Int): Editor
        fun putLong(key: String, value: Long): Editor
        fun putFloat(key: String, value: Float): Editor
        fun putBoolean(key: String, value: Boolean): Editor
        fun remove(key: String): Editor
        fun clear(): Editor
    }
}

expect interface PlatformPreferencesListener {
    fun onChanged(prefs: PlatformPreferences, key: String)
}

expect class PlatformPreferencesImpl: PlatformPreferences {
    override fun addListener(listener: PlatformPreferencesListener): PlatformPreferencesListener
    override fun removeListener(listener: PlatformPreferencesListener)

    override fun getString(key: String, defValue: String?): String?
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>?
    override fun getInt(key: String, defValue: Int?): Int?
    override fun getLong(key: String, defValue: Long?): Long?
    override fun getFloat(key: String, defValue: Float?): Float?
    override fun getBoolean(key: String, defValue: Boolean?): Boolean?
    override operator fun contains(key: String): Boolean

    override fun edit(action: PlatformPreferences.Editor.() -> Unit)

    open class EditorImpl: PlatformPreferences.Editor {
        override fun putString(key: String, value: String): PlatformPreferences.Editor
        override fun putStringSet(key: String, values: Set<String>): PlatformPreferences.Editor
        override fun putInt(key: String, value: Int): PlatformPreferences.Editor
        override fun putLong(key: String, value: Long): PlatformPreferences.Editor
        override fun putFloat(key: String, value: Float): PlatformPreferences.Editor
        override fun putBoolean(key: String, value: Boolean): PlatformPreferences.Editor
        override fun remove(key: String): PlatformPreferences.Editor
        override fun clear(): PlatformPreferences.Editor
    }
}
