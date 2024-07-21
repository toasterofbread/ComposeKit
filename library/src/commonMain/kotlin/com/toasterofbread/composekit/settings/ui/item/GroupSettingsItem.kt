package dev.toastbits.composekit.settings.ui.item

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.settings.ui.vibrant_accent
import dev.toastbits.composekit.platform.PreferencesProperty
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class GroupSettingsItem(var title: StringResource?): SettingsItem() {
    override fun getProperties(): List<PreferencesProperty<*>> = emptyList()
    override suspend fun resetValues() {}

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        title?.also {
            Text(stringResource(it), color = settings_interface.theme.vibrant_accent, fontSize = 20.sp, fontWeight = FontWeight.Light)
        }
    }
}
