package com.toasterofbread.toastercomposetools.settings.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toasterofbread.toastercomposetools.platform.PlatformPreferences
import com.toasterofbread.toastercomposetools.settings.ui.SettingsInterface
import com.toasterofbread.toastercomposetools.settings.ui.SettingsPage
import com.toasterofbread.toastercomposetools.utils.composable.ResizableOutlinedTextField

// TODO Styling
class SettingsTextFieldItem(
    val state: BasicSettingsValueState<String>,
    val title: String?,
    val subtitle: String?,
    val single_line: Boolean = true
): SettingsItem() {
    override fun initialiseValueStates(prefs: PlatformPreferences, default_provider: (String) -> Any) {
        state.init(prefs, default_provider)
    }

    override fun releaseValueStates(prefs: PlatformPreferences) {
        state.release(prefs)
    }

    override fun resetValues() {
        state.reset()
    }

    @Composable
    override fun Item(settings_interface: SettingsInterface, openPage: (Int, Any?) -> Unit, openCustomPage: (SettingsPage) -> Unit) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            ItemTitleText(title, settings_interface.theme)
            ItemText(subtitle, settings_interface.theme)
            ResizableOutlinedTextField(state.get(), { state.set(it) }, Modifier.fillMaxWidth(), singleLine = single_line)
        }
    }
}
