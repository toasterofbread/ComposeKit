package dev.toastbits.composekit.settings.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.platform.PreferencesProperty

class SubpageSettingsItem(
    val title: String,
    val subtitle: String?,
    val target_page: Int,
    val target_page_param: Any?
): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = emptyList()
    override fun resetValues() {}

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        val theme = settings_interface.theme

        Button(
            { openPage(target_page, target_page_param) },
            Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.vibrant_accent,
                contentColor = theme.on_accent
            )
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, color = theme.on_accent)
                ItemText(subtitle, theme)
            }
        }
    }
}
