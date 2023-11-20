package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage
import com.toasterofbread.composekit.utils.composable.LinkifyText

class SettingsItemInfoText(val text: String): EmptySettingsItem() {
    override fun getKeys(): List<String> = emptyList()

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
