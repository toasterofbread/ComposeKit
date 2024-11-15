package dev.toastbits.composekit.platform.preferences.impl.group.impl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.composekit.library.generated.resources.Res
import dev.toastbits.composekit.library.generated.resources.pref_interface_animate_pane_resize_title
import dev.toastbits.composekit.library.generated.resources.pref_interface_show_pane_resize_handles
import dev.toastbits.composekit.library.generated.resources.pref_interface_show_pane_resize_handles_on_hover
import dev.toastbits.composekit.library.generated.resources.prefs_group_interface_description
import dev.toastbits.composekit.library.generated.resources.prefs_group_interface_title
import dev.toastbits.composekit.platform.preferences.PlatformPreferences
import dev.toastbits.composekit.platform.preferences.PreferencesGroupImpl
import dev.toastbits.composekit.platform.preferences.PreferencesProperty
import dev.toastbits.composekit.platform.preferences.impl.group.ComposeKitInterfacePreferencesGroup
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem
import dev.toastbits.composekit.settings.ui.component.item.ToggleSettingsItem
import dev.toastbits.composekit.utils.composable.pane.model.PLATFORM_DEFAULT_USE_PANE_RESIZE_ANIMATION_SPEC
import org.jetbrains.compose.resources.stringResource

open class ComposeKitInterfacePreferencesGroupImpl(
    groupKey: String,
    preferences: PlatformPreferences
): PreferencesGroupImpl(groupKey, preferences), ComposeKitInterfacePreferencesGroup {
    @Composable
    override fun getTitle(): String = stringResource(Res.string.prefs_group_interface_title)

    @Composable
    override fun getDescription(): String = stringResource(Res.string.prefs_group_interface_description)

    @Composable
    override fun getIcon(): ImageVector = Icons.Default.GridView

    override val SHOW_PANE_RESIZE_HANDLES: PreferencesProperty<Boolean> by
        property(
            getName = { stringResource(Res.string.pref_interface_show_pane_resize_handles) },
            getDescription = { null },
            getDefaultValue = { true }
        )

    override val SHOW_PANE_RESIZE_HANDLES_ON_HOVER: PreferencesProperty<Boolean> by
        property(
            getName = { stringResource(Res.string.pref_interface_show_pane_resize_handles_on_hover) },
            getDescription = { null },
            getDefaultValue = { true }
        )

    override val ANIMATE_PANE_RESIZE: PreferencesProperty<Boolean> by
        property(
            getName = { stringResource(Res.string.pref_interface_animate_pane_resize_title) },
            getDescription = { null },
            getDefaultValue = { PLATFORM_DEFAULT_USE_PANE_RESIZE_ANIMATION_SPEC }
        )

    override fun getConfigurationItems(): List<SettingsItem> =
        listOf(
            ToggleSettingsItem(SHOW_PANE_RESIZE_HANDLES),
            ToggleSettingsItem(SHOW_PANE_RESIZE_HANDLES_ON_HOVER),
            ToggleSettingsItem(ANIMATE_PANE_RESIZE)
        )
}
