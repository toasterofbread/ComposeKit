package dev.toastbits.composekit.platform

import kotlin.properties.PropertyDelegateProvider
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.serializer
import kotlinx.serialization.KSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import dev.toastbits.composekit.utils.composable.OnChangedEffect
import kotlinx.serialization.json.Json

@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
abstract class PreferencesGroupImpl(
    override val group_key: String?,
    val prefs: PlatformPreferences
): PreferencesGroup {
    protected open fun getUnregisteredProperties(): List<PreferencesProperty<*>> = emptyList()

    override fun getAllProperties(): List<PreferencesProperty<*>> = all_properties + getUnregisteredProperties()

    protected inline fun <reified T: Any> property(
        noinline getName: @Composable () -> String,
        noinline getDescription: @Composable () -> String?,
        noinline getDefaultValue: () -> T,
        noinline isHidden: () -> Boolean = { false }
    ): PropertyDelegateProvider<Any?, PreferencesProperty<T>> {
        val defaultValueProvider: () -> T = getDefaultValue
        return PropertyDelegateProvider { _, property ->
            check(T::class !is Enum<*>) { "Enum property '$property' must use enumProperty()" }

            val property: PreferencesProperty<T> =
                object : PrefsProperty<T>(key = property.name) {
                    @Composable
                    override fun getName(): String = getName()
                    @Composable
                    override fun getDescription(): String? = getDescription()

                    override suspend fun getDefaultValue(): T = defaultValueProvider()
                    @Composable
                    override fun getDefaultValueComposable(): T = defaultValueProvider()
                    override fun isHidden(): Boolean = isHidden()
                }
            onPropertyAdded(property)
            return@PropertyDelegateProvider property
        }
    }

    protected inline fun <reified T: Any> resourceDefaultValueProperty(
        noinline getName: @Composable () -> String,
        noinline getDescription: @Composable () -> String?,
        noinline getDefaultValueSuspending: suspend () -> T,
        noinline getDefaultValueComposable: @Composable () -> T,
        noinline isHidden: () -> Boolean = { false }
    ): PropertyDelegateProvider<Any?, PreferencesProperty<T>> {
        return PropertyDelegateProvider { _, property ->
            check(T::class !is Enum<*>) { "Enum property '$property' must use enumProperty()" }

            val property: PreferencesProperty<T> =
                object : PrefsProperty<T>(key = property.name) {
                    @Composable
                    override fun getName(): String = getName()
                    @Composable
                    override fun getDescription(): String? = getDescription()

                    override suspend fun getDefaultValue(): T = getDefaultValueSuspending()
                    @Composable
                    override fun getDefaultValueComposable(): T = getDefaultValueComposable()
                    override fun isHidden(): Boolean = isHidden()
                }
            onPropertyAdded(property)
            return@PropertyDelegateProvider property
        }
    }

    protected inline fun <reified T: Enum<T>> enumProperty(
        noinline getName: @Composable () -> String,
        noinline getDescription: @Composable () -> String?,
        noinline getDefaultValue: () -> T,
        noinline isHidden: () -> Boolean = { false }
    ): PropertyDelegateProvider<Any?, PreferencesProperty<T>> {
        val defaultValueProvider: () -> T = getDefaultValue
        return PropertyDelegateProvider { _, property ->
            val property: PreferencesProperty<T> =
                object : EnumPrefsProperty<T>(
                    key = property.name,
                    entries = enumValues<T>().toList()
                ) {
                    @Composable
                    override fun getName(): String = getName()
                    @Composable
                    override fun getDescription(): String? = getDescription()

                    override suspend fun getDefaultValue(): T = defaultValueProvider()
                    @Composable
                    override fun getDefaultValueComposable(): T = defaultValueProvider()
                    override fun isHidden(): Boolean = isHidden()
                }
            onPropertyAdded(property)
            return@PropertyDelegateProvider property
        }
    }

    protected inline fun <reified T: Any> serialisableProperty(
        noinline getName: @Composable () -> String,
        noinline getDescription: @Composable () -> String?,
        noinline getDefaultValue: () -> T,
        json: Json? = null
    ): PropertyDelegateProvider<Any?, PreferencesProperty<T>> {
        val defaultValueProvider: () -> T = getDefaultValue
        return PropertyDelegateProvider { _, property ->
            val property: PreferencesProperty<T> =
                object : SerialisablePrefsProperty<T>(
                    key = property.name,
                    serialiser = serializer<T>(),
                    jsonOverride = json
                ) {
                    @Composable
                    override fun getName(): String = getName()
                    @Composable
                    override fun getDescription(): String? = getDescription()

                    override suspend fun getDefaultValue(): T = defaultValueProvider()
                    @Composable
                    override fun getDefaultValueComposable(): T = defaultValueProvider()
                }
            onPropertyAdded(property)
            return@PropertyDelegateProvider property
        }
    }

    protected inline fun <reified T: Any> nullableSerialisableProperty(
        noinline getName: @Composable () -> String,
        noinline getDescription: @Composable () -> String?,
        noinline getDefaultValue: () -> T?,
        json: Json? = null
    ): PropertyDelegateProvider<Any?, PreferencesProperty<T?>> {
        val defaultValueProvider: () -> T? = getDefaultValue
        return PropertyDelegateProvider { _, property ->
            val property: PreferencesProperty<T?> =
                object : SerialisablePrefsProperty<T?>(
                    key = property.name,
                    serialiser = serializer<T?>(),
                    jsonOverride = json
                ) {
                    @Composable
                    override fun getName(): String = getName()
                    @Composable
                    override fun getDescription(): String? = getDescription()

                    override suspend fun getDefaultValue(): T? = defaultValueProvider()
                    @Composable
                    override fun getDefaultValueComposable(): T? = defaultValueProvider()
                }
            onPropertyAdded(property)
            return@PropertyDelegateProvider property
        }
    }

    private val all_properties: MutableList<PreferencesProperty<*>> = mutableListOf()

    fun onPropertyAdded(property: PreferencesProperty<*>) {
        all_properties.add(property)
    }

    private fun formatPropertyKey(property_key: String): String {
        if (group_key == null) {
            return property_key
        }
        return group_key + "_" + property_key
    }

    @Suppress("UNCHECKED_CAST")
    protected abstract inner class PrefsProperty<T>(key: String): PreferencesProperty<T> {
        override val key: String = formatPropertyKey(key)

        override suspend fun get(): T =
            when (val default_value: T = getDefaultValue()) {
                is Boolean -> prefs.getBoolean(key, default_value)
                is Float -> prefs.getFloat(key, default_value)
                is Int -> prefs.getInt(key, default_value)
                is Long -> prefs.getLong(key, default_value)
                is String -> prefs.getString(key, default_value)
                is Set<*> -> prefs.getStringSet(key, default_value as Set<String>)
                is Enum<*> -> throw IllegalStateException("Use EnumPrefsProperty")
                else -> throw NotImplementedError("$key $default_value ${default_value!!::class.simpleName}")
            } as T

        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        override fun set(value: T, editor: PlatformPreferences.Editor?) =
            (editor ?: prefs).edit {
                when (value) {
                    null -> remove(key)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is String -> putString(key, value)
                    is Set<*> -> putStringSet(key, value as Set<String>)
                    is Enum<*> -> throw IllegalStateException("Use EnumPrefsProperty")
                    else -> throw NotImplementedError("$key ${value!!::class.simpleName}")
                }
            }

        override fun set(data: JsonElement, editor: PlatformPreferences.Editor?) =
            when (data) {
                is JsonArray -> set(data.map { it.jsonPrimitive.content }.toSet() as T, editor)
                is JsonPrimitive -> {
                    val value: T = (
                        data.booleanOrNull
                        ?: data.intOrNull
                        ?: data.longOrNull
                        ?: data.floatOrNull
                        ?: data.contentOrNull
                    ) as T

                    set(value, editor)
                }
                is JsonObject -> throw IllegalStateException("PrefsProperty ($this) data is JsonObject ($data)")
            }

        override fun reset() =
            prefs.edit {
                remove(key)
            }

        override fun serialise(value: Any?): JsonElement =
            when (value) {
                null -> JsonPrimitive(null)
                is Boolean -> JsonPrimitive(value)
                is Float -> JsonPrimitive(value)
                is Int -> JsonPrimitive(value)
                is Long -> JsonPrimitive(value)
                is String -> JsonPrimitive(value)
                is Set<*> -> JsonArray((value as Set<String>).map { JsonPrimitive(it) })
                is Enum<*> -> throw IllegalStateException("Use EnumPrefsProperty")
                else -> throw NotImplementedError("$key ${value::class.simpleName}")
            }

        private var previousValue: T? = null
        private var previousValueSet: Boolean = false

        @Composable
        override fun observe(): MutableState<T> {
            val coroutine_scope: CoroutineScope = rememberCoroutineScope()

            val default_value: T = getDefaultValueComposable()
            val state: MutableState<T> = remember { mutableStateOf(if (previousValueSet) previousValue as T else default_value) }
            var set_to: T by remember { mutableStateOf(state.value) }

            LaunchedEffect(this) {
                set_to = get()
                state.value = get()
                previousValue = state.value
                previousValueSet = true
            }

            LaunchedEffect(state.value) {
                if (state.value != set_to) {
                    set_to = state.value
                    set(set_to)
                    previousValue = state.value
                    previousValueSet = true
                }
            }

            OnChangedEffect(this) {
                previousValueSet = false
                state.value = get()
                previousValue = state.value
                previousValueSet = true
            }

            DisposableEffect(this) {
                val listener: PlatformPreferencesListener =
                    prefs.addListener(
                        PlatformPreferencesListener { key ->
                            if (key == this@PrefsProperty.key) {
                                coroutine_scope.launch {
                                    set_to = get()
                                    state.value = set_to
                                    previousValue = state.value
                                    previousValueSet = true
                                }
                            }
                        }
                    )

                onDispose {
                    prefs.removeListener(listener)

                    if (state.value != set_to) {
                        set_to = state.value
                        set(set_to)
                    }
                }
            }

            return state
        }

        override fun toString(): String =
            "PrefsProperty<T>(key=$key)"
    }

    protected abstract inner class EnumPrefsProperty<T: Enum<T>>(
        key: String,
        val entries: List<T>
    ): PrefsProperty<T>(key) {
        override suspend fun get(): T =
            entries[prefs.getInt(key, getDefaultValue().ordinal)!!]

        override fun set(value: T, editor: PlatformPreferences.Editor?) =
            (editor ?: prefs).edit {
                putInt(key, value.ordinal)
            }

        override fun set(data: JsonElement, editor: PlatformPreferences.Editor?) =
            set(entries[data.jsonPrimitive.int], editor)

        override fun serialise(value: Any?): JsonElement =
            JsonPrimitive((value as T?)?.ordinal)

        override fun toString(): String =
            "EnumPrefsProperty(key=$key)"
    }

    protected abstract inner class SerialisablePrefsProperty<T>(
        key: String,
        val serialiser: KSerializer<T>,
        private val jsonOverride: Json?
    ): PrefsProperty<T>(key) {
        private val json: Json
            get() = jsonOverride ?: prefs.json

        override suspend fun get(): T =
            prefs.getSerialisable(key, getDefaultValue(), serialiser, json)

        override fun set(value: T, editor: PlatformPreferences.Editor?) =
            (editor ?: prefs).edit {
                putSerialisable(key, value, serialiser, this@SerialisablePrefsProperty.json)
            }

        override fun set(data: JsonElement, editor: PlatformPreferences.Editor?) {
            val value: T

            if (data is JsonPrimitive) {
                value = json.decodeFromString(serialiser, data.content)
            }
            else {
                value = json.decodeFromJsonElement(serialiser, data)
            }

            set(value, editor)
        }

        override fun serialise(value: Any?): JsonElement =
            json.encodeToJsonElement(serialiser, value as T)

        override fun toString(): String =
            "SerialisablePrefsProperty(key=$key)"
    }
}

private fun Any.edit(action: PlatformPreferences.Editor.() -> Unit) {
    if (this is PlatformPreferences.Editor) {
        action(this)
    }
    else if (this is PlatformPreferences) {
        edit {
            action(this)
        }
    }
    else {
        throw NotImplementedError(this::class.toString())
    }
}
