package com.toasterofbread.toastercomposetools.settings.ui.item

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.toasterofbread.toastercomposetools.platform.PlatformPreferences
import com.toasterofbread.toastercomposetools.settings.ui.SettingsInterface
import com.toasterofbread.toastercomposetools.settings.ui.SettingsPage

class SettingsItemInfoText(val text: String): SettingsItem() {
    override fun initialiseValueStates(prefs: PlatformPreferences, default_provider: (String) -> Any) {}
    override fun releaseValueStates(prefs: PlatformPreferences) {}
    override fun resetValues() {}

    @Composable
    override fun Item(settings_interface: SettingsInterface, openPage: (Int, Any?) -> Unit, openCustomPage: (SettingsPage) -> Unit) {
        Text(text)
    }
}
