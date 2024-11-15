package dev.toastbits.composekit.platform.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem

interface PreferencesGroup {
    val groupKey: String?
    fun getAllProperties(): List<PreferencesProperty<*>>

    @Composable
    fun getTitle(): String
    @Composable
    fun getDescription(): String
    @Composable
    fun getIcon(): ImageVector

    fun getConfigurationItems(): List<SettingsItem>
}
