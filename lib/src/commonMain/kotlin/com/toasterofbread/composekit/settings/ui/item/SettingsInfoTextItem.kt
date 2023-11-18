package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.runtime.Composable
import com.toasterofbread.composekit.platform.PlatformPreferences
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage
import com.toasterofbread.composekit.utils.composable.LinkifyText

class SettingsItemInfoText(val text: String): EmptySettingsItem() {
    @Composable
    override fun Item(settings_interface: SettingsInterface, openPage: (Int, Any?) -> Unit, openCustomPage: (SettingsPage) -> Unit) {
        LinkifyText(text, settings_interface.theme.vibrant_accent)
    }
}
