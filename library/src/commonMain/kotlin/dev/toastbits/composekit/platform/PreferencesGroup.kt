package dev.toastbits.composekit.platform

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem

interface PreferencesGroup {
    val group_key: String?
    fun getAllProperties(): List<PreferencesProperty<*>>

    @Composable
    fun getTitle(): String
    @Composable
    fun getDescription(): String
    @Composable
    fun getIcon(): ImageVector

    fun getConfigurationItems(): List<SettingsItem>
}
