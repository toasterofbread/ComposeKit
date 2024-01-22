package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage

class ComposableSettingsItem(
    val settings_keys: List<String> = emptyList(),
    val resetSettingsValues: () -> Unit = {},
    val composable: @Composable SettingsInterface.(Modifier) -> Unit
): EmptySettingsItem() {
    override fun getKeys(): List<String> = settings_keys

    override fun resetValues() {
        super.resetValues()
        resetSettingsValues()
    }

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        composable(settings_interface, modifier)
    }
}
