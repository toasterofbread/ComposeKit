package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.platform.PreferencesProperty

class ComposableSettingsItem(
    val settings_properties: List<PreferencesProperty<*>> = emptyList(),
    val resetSettingsValues: () -> Unit = {},
    val composable: @Composable (Modifier) -> Unit
): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = settings_properties

    override suspend fun resetValues() {
        resetSettingsValues()
    }

    @Composable
    override fun Item(
        modifier: Modifier
    ) {
        composable(modifier)
    }
}
