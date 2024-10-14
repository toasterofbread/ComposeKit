package dev.toastbits.composekit.platform

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import dev.toastbits.composekit.utils.composable.OnChangedEffect
import kotlinx.serialization.json.JsonElement

interface PreferencesProperty<T>: ReadOnlyProperty<Any?, PreferencesProperty<T>> {
    val key: String

    @Composable
    fun getName(): String
    @Composable
    fun getDescription(): String?

    suspend fun get(): T
    fun set(value: T, editor: PlatformPreferences.Editor? = null)
    fun set(data: JsonElement, editor: PlatformPreferences.Editor? = null)
    fun reset()

    fun serialise(value: Any?): JsonElement

    suspend fun getDefaultValue(): T
    @Composable
    fun getDefaultValueComposable(): T

    fun isHidden(): Boolean = false

    @Composable
    fun observe(): MutableState<T>

    override fun getValue(thisRef: Any?, property: KProperty<*>): PreferencesProperty<T> = this

    fun <O> getConvertedProperty(fromProperty: (T) -> O, toProperty: (O) -> T): PreferencesProperty<O> {
        val base: PreferencesProperty<T> = this
        return object : PreferencesProperty<O> {
            override val key: String get() = base.key
            @Composable
            override fun getName(): String = base.getName()
            @Composable
            override fun getDescription(): String? = base.getDescription()

            override suspend fun get(): O = fromProperty(base.get())
            override fun set(value: O, editor: PlatformPreferences.Editor?) { base.set(toProperty(value), editor) }
            override fun set(data: JsonElement, editor: PlatformPreferences.Editor?) { base.set(data, editor) }
            override fun reset() { base.reset() }

            @Suppress("UNCHECKED_CAST")
            override fun serialise(value: Any?): JsonElement = base.serialise(toProperty(value as O))

            override suspend fun getDefaultValue(): O = fromProperty(base.getDefaultValue())
            @Composable
            override fun getDefaultValueComposable(): O = fromProperty(base.getDefaultValueComposable())

            override fun isHidden(): Boolean = base.isHidden()

            @Composable
            override fun observe(): MutableState<O> {
                var base_value: T by base.observe()

                val state: MutableState<O> = remember { mutableStateOf(fromProperty(base_value)) }
                var set_to: O by remember { mutableStateOf(state.value) }

                LaunchedEffect(state.value) {
                    if (state.value != set_to) {
                        set_to = state.value
                        set(set_to)
                    }
                }

                OnChangedEffect(this) {
                    state.value = get()
                }

                LaunchedEffect(base_value) {
                    if (base_value != set_to) {
                        set_to = fromProperty(base_value)
                        state.value = set_to
                    }
                }

                return state
            }
        }
    }
}
