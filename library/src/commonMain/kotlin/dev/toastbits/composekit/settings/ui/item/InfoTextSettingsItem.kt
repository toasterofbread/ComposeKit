package dev.toastbits.composekit.settings.ui.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.settings.ui.theme.vibrant_accent
import dev.toastbits.composekit.utils.composable.LinkifyText
import dev.toastbits.composekit.platform.PreferencesProperty
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class InfoTextSettingsItem(val text: StringResource): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = emptyList()
    override suspend fun resetValues() {}

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        LinkifyText(settings_interface.context, stringResource(text), settings_interface.theme.vibrant_accent)
    }
}
