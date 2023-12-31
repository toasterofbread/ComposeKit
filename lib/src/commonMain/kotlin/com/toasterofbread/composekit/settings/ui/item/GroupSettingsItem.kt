package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage

class GroupSettingsItem(var title: String?): EmptySettingsItem() {
    override fun getKeys(): List<String> = emptyList()

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        title?.also {
            Text(it, color = settings_interface.theme.vibrant_accent, fontSize = 20.sp, fontWeight = FontWeight.Light)
        }
    }
}
