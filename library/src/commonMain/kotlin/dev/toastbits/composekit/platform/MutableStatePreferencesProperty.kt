package dev.toastbits.composekit.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import kotlinx.serialization.json.JsonElement

class MutableStatePreferencesProperty<T>(
    private val state: MutableState<T>,
    private val getPropertyName: @Composable () -> String,
    private val getPropertyDescription: @Composable () -> String?,
    private val getPropertyDefaultValue: suspend () -> T = { throw IllegalStateException("Default value not provided to MutableStatePreferencesProperty") },
    private val getPropertyDefaultValueComposable: @Composable () -> T = { throw IllegalStateException("Default value not provided to MutableStatePreferencesProperty") }
): PreferencesProperty<T> {
    override val key: String get() = throw IllegalStateException()

    override suspend fun get(): T = state.value

    override suspend fun getDefaultValue(): T = getPropertyDefaultValue()

    @Composable
    override fun getDefaultValueComposable(): T = getPropertyDefaultValueComposable()

    @Composable
    override fun getName(): String = getPropertyName()

    @Composable
    override fun getDescription(): String? = getPropertyDescription()

    @Composable
    override fun observe(): MutableState<T> = state

    override fun reset() = throw IllegalStateException()

    override fun serialise(value: Any?): JsonElement = throw IllegalStateException()

    override fun set(data: JsonElement, editor: PlatformPreferences.Editor?) = throw IllegalStateException()

    override fun set(value: T, editor: PlatformPreferences.Editor?) {
        state.value = value
    }
}
