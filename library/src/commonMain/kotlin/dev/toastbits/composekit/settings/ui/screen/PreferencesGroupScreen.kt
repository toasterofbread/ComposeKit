package dev.toastbits.composekit.settings.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem

data class PreferencesGroupScreen(
    val group: PreferencesGroup
): Screen {
    override val title: String
        @Composable get() = group.getTitle()

    @Composable
    override fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues) {
        val settingsItems: List<SettingsItem> = remember(group) { group.getConfigurationItems() }

        ScrollBarLazyColumn(
            modifier,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(settingsItems) { item ->
                item.Item(Modifier.fillMaxWidth())
            }
        }
    }
}
