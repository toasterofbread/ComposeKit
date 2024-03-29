@file:Suppress("MemberVisibilityCanBePrivate")

package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.toasterofbread.composekit.platform.PlatformPreferences
import com.toasterofbread.composekit.platform.PlatformPreferencesListener
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage
import com.toasterofbread.composekit.settings.ui.Theme
import com.toasterofbread.composekit.utils.composable.LinkifyText
import com.toasterofbread.composekit.utils.composable.WidthShrinkText

val SETTINGS_ITEM_ROUNDED_SHAPE = RoundedCornerShape(20.dp)

abstract class SettingsItem {
    private var initialised = false
    fun initialise(prefs: PlatformPreferences, default_provider: (String) -> Any) {
        if (initialised) {
            return
        }
        initialiseValueStates(prefs, default_provider)
        initialised = true
    }

    abstract fun initialiseValueStates(prefs: PlatformPreferences, default_provider: (String) -> Any)
    protected abstract fun releaseValueStates(prefs: PlatformPreferences)

    abstract fun setEnableAutosave(value: Boolean)
    abstract fun PlatformPreferences.Editor.saveItem()
    abstract fun resetValues()

    abstract fun getKeys(): List<String>

    @Composable
    abstract fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    )
    
    companion object {
        @Composable
        fun ItemTitleText(text: String?, theme: Theme, modifier: Modifier = Modifier, max_lines: Int = 1) {
            if (text?.isNotBlank() == true) {
                WidthShrinkText(
                    text,
                    modifier,
                    style = MaterialTheme.typography.titleMedium.copy(color = theme.on_background),
                    max_lines = max_lines
                )
            }
        }

        @Composable
        fun ItemText(
            text: String?,
            theme: Theme,
            colour: Color =
            theme.on_background.copy(alpha = 0.75f),
            linkify: Boolean = true
        ) {
            if (text?.isNotBlank() == true) {
                val style: TextStyle = MaterialTheme.typography.bodySmall.copy(color = colour)
                if (linkify) LinkifyText(text, theme.accent, style = style)
                else Text(text, style = style)
            }
        }
    }
}

interface BasicSettingsValueState<T: Any> {
    fun get(): T
    fun set(value: T)

    fun init(prefs: PlatformPreferences, defaultProvider: (String) -> Any): BasicSettingsValueState<T>
    fun release(prefs: PlatformPreferences)

    fun setEnableAutosave(value: Boolean)
    fun reset()
    fun PlatformPreferences.Editor.save()
    fun getDefault(defaultProvider: (String) -> Any): T

    @Composable
    fun onChanged(key: Any?, action: (T) -> Unit)

    fun getKeys(): List<String>
}

@Suppress("UNCHECKED_CAST")
class SettingsValueState<T: Any>(
    val key: String,
    private val onChanged: ((value: T) -> Unit)? = null,
    private val getValueConverter: (Any?) -> T? = { it as T },
    private val setValueConverter: (T) -> Any = { it }
): BasicSettingsValueState<T>, State<T> {
    var autosave: Boolean = true

    private lateinit var defaultProvider: (String) -> Any
    private lateinit var prefs: PlatformPreferences
    private var pref_listener: PlatformPreferencesListener? = null

    private val change_listeners: MutableList<(T) -> Unit> = mutableListOf()

    private var _value: T? by mutableStateOf(null)
    override val value: T get() = _value!!

    override fun get(): T = _value!!
    override fun set(value: T) {
        check(_value != null) { "State has not been initialised" }

        if (_value == value) {
            return
        }

        _value = value
        if (autosave) {
            prefs.edit {
                save()
            }
        }

        onChanged?.invoke(value)
        for (listener in change_listeners) {
            listener(_value!!)
        }
    }

    @Composable
    override fun onChanged(key: Any?, action: (T) -> Unit) {
        DisposableEffect(key) {
            val listener: (T) -> Unit = action
            change_listeners.add(listener)

            onDispose {
                change_listeners.remove(listener)
            }
        }
    }

    private fun updateValue() {
        val default = defaultProvider(key) as T
        _value = getValueConverter(when (default) {
            is Boolean -> prefs.getBoolean(key, default as Boolean)
            is Float -> prefs.getFloat(key, default as Float)
            is Int -> prefs.getInt(key, default as Int)
            is Long -> prefs.getLong(key, default as Long)
            is String -> prefs.getString(key, default as String)
            is Set<*> -> prefs.getStringSet(key, default as Set<String>)
            else -> throw ClassCastException()
        })
    }

    override fun init(prefs: PlatformPreferences, defaultProvider: (String) -> Any): SettingsValueState<T> {
        this.prefs = prefs
        this.defaultProvider = defaultProvider

        if (_value != null) {
            return this
        }

        updateValue()

        pref_listener =
            object : PlatformPreferencesListener {
                override fun onChanged(prefs: PlatformPreferences, key: String) {
                    if (key == this@SettingsValueState.key) {
                        updateValue()
                    }
                }
            }
            .also {
                prefs.addListener(it)
            }

        return this
    }

    override fun release(prefs: PlatformPreferences) {
        pref_listener?.also {
            prefs.removeListener(it)
        }
    }

    override fun setEnableAutosave(value: Boolean) {
        autosave = value
    }

    override fun reset() {
        _value = getValueConverter(defaultProvider(key) as T)!!
        prefs.edit {
            save()
        }
        onChanged?.invoke(_value!!)
        for (listener in change_listeners) {
            listener(_value!!)
        }
    }

    override fun PlatformPreferences.Editor.save() {
        val value: Any = this@SettingsValueState.setValueConverter(get())
        when (value) {
            defaultProvider(key) -> remove(key)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is String -> putString(key, value)
            is Set<*> -> putStringSet(key, value as Set<String>)
            else -> throw ClassCastException(value::class.toString())
        }
    }

    override fun getDefault(defaultProvider: (String) -> Any): T = defaultProvider(key) as T

    override fun getKeys(): List<String> = listOf(key)
}

abstract class EmptySettingsItem(): SettingsItem() {
    override fun initialiseValueStates(prefs: PlatformPreferences, default_provider: (String) -> Any) {}
    override fun releaseValueStates(prefs: PlatformPreferences) {}
    override fun setEnableAutosave(value: Boolean) {}
    override fun PlatformPreferences.Editor.saveItem() {}
    override fun resetValues() {}
}
