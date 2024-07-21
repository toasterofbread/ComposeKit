package dev.toastbits.composekit.settings.ui.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.platform.PreferencesProperty

class ComposableSettingsItem(
    val settings_properties: List<PreferencesProperty<*>> = emptyList(),
    val resetSettingsValues: () -> Unit = {},
    val composable: @Composable SettingsInterface.(Modifier) -> Unit
): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = settings_properties

    override suspend fun resetValues() {
        resetSettingsValues()
    }

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        composable(settings_interface, modifier)
    }
}
