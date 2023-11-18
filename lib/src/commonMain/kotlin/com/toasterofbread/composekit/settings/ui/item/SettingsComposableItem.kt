package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.runtime.Composable
import com.toasterofbread.composekit.platform.PlatformPreferences
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage
import com.toasterofbread.composekit.settings.ui.Theme

class SettingsComposableItem(val composable: @Composable Theme.() -> Unit): EmptySettingsItem() {
    @Composable
    override fun Item(settings_interface: SettingsInterface, openPage: (Int, Any?) -> Unit, openCustomPage: (SettingsPage) -> Unit) {
        composable(settings_interface.theme)
    }
}
