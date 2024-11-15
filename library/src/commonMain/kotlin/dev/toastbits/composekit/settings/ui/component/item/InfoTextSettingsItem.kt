package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.settings.ui.vibrant_accent
import dev.toastbits.composekit.utils.composable.LinkifyText
import dev.toastbits.composekit.platform.preferences.PreferencesProperty
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class InfoTextSettingsItem(val text: StringResource): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = emptyList()
    override suspend fun resetValues() {}

    @Composable
    override fun Item(
        modifier: Modifier
    ) {
        LinkifyText(
            stringResource(text),
            LocalApplicationTheme.current.vibrant_accent,
            modifier
        )
    }
}
