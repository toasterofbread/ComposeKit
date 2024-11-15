package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.platform.preferences.PreferencesProperty

class ComposableSettingsItem(
    val settings_properties: List<PreferencesProperty<*>> = emptyList(),
    val resetSettingsValues: () -> Unit = {},
    val shouldShowItem: @Composable () -> Boolean = { true },
    val content: @Composable (Modifier) -> Unit
): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = settings_properties

    override suspend fun resetValues() {
        resetSettingsValues()
    }

    @Composable
    override fun Item(
        modifier: Modifier
    ) {
        content(modifier)
    }

    @Composable
    override fun showItem(): Boolean = shouldShowItem()
}
