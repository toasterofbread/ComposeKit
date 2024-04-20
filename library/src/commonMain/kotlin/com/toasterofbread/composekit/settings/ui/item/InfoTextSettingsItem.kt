package dev.toastbits.composekit.settings.ui.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.utils.composable.LinkifyText
import dev.toastbits.composekit.platform.PreferencesProperty

class InfoTextSettingsItem(val text: String): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = emptyList()
    override fun resetValues() {}

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        LinkifyText(text, settings_interface.theme.vibrant_accent)
    }
}
