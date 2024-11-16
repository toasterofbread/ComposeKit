package dev.toastbits.composekit.platform.preferences.impl.group.impl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.toastbits.composekit.library.generated.resources.Res
import dev.toastbits.composekit.library.generated.resources.pref_interface_animate_pane_resize_title
import dev.toastbits.composekit.library.generated.resources.pref_interface_initial_pane_ratio_remember_mode_option_ratio
import dev.toastbits.composekit.library.generated.resources.pref_interface_initial_pane_ratio_remember_mode_option_size_dp
import dev.toastbits.composekit.library.generated.resources.pref_interface_initial_pane_ratio_remember_mode_title
import dev.toastbits.composekit.library.generated.resources.pref_interface_remember_initial_pane_ratios_title
import dev.toastbits.composekit.library.generated.resources.pref_interface_show_pane_resize_handles_title
import dev.toastbits.composekit.library.generated.resources.pref_interface_show_pane_resize_handles_on_hover_title
import dev.toastbits.composekit.library.generated.resources.prefs_group_interface_description
import dev.toastbits.composekit.library.generated.resources.prefs_group_interface_title
import dev.toastbits.composekit.platform.preferences.PlatformPreferences
import dev.toastbits.composekit.platform.preferences.PreferencesGroupImpl
import dev.toastbits.composekit.platform.preferences.PreferencesProperty
import dev.toastbits.composekit.platform.preferences.impl.group.ComposeKitInterfacePreferencesGroup
import dev.toastbits.composekit.settings.ui.component.item.DropdownSettingsItem
import dev.toastbits.composekit.settings.ui.component.item.SettingsItem
import dev.toastbits.composekit.settings.ui.component.item.ToggleSettingsItem
import dev.toastbits.composekit.utils.composable.pane.model.InitialPaneRatioSource
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
            getName = { stringResource(Res.string.pref_interface_show_pane_resize_handles_title) },
            getDescription = { null },
            getDefaultValue = { true }
        )

    override val SHOW_PANE_RESIZE_HANDLES_ON_HOVER: PreferencesProperty<Boolean> by
        property(
            getName = { stringResource(Res.string.pref_interface_show_pane_resize_handles_on_hover_title) },
            getDescription = { null },
            getDefaultValue = { true }
        )

    override val ANIMATE_PANE_RESIZE: PreferencesProperty<Boolean> by
        property(
            getName = { stringResource(Res.string.pref_interface_animate_pane_resize_title) },
            getDescription = { null },
            getDefaultValue = { PLATFORM_DEFAULT_USE_PANE_RESIZE_ANIMATION_SPEC }
        )

    override val REMEMBER_INITIAL_PANE_RATIOS: PreferencesProperty<Boolean> by
        property(
            getName = { stringResource(Res.string.pref_interface_remember_initial_pane_ratios_title) },
            getDescription = { null },
            getDefaultValue = { true }
        )

    override val REMEMBERED_INITIAL_PANE_RATIOS: PreferencesProperty<Map<String, InitialPaneRatioSource>> by
        serialisableProperty(
            getName = { "" },
            getDescription = { null },
            getDefaultValue = { emptyMap() },
            isHidden = { true }
        )

    override val INITIAL_PANE_RATIO_REMEMBER_MODE: PreferencesProperty<InitialPaneRatioSource.Remembered.RememberMode> by
        enumProperty(
            getName = { stringResource(Res.string.pref_interface_initial_pane_ratio_remember_mode_title) },
            getDescription = { null },
            getDefaultValue = { InitialPaneRatioSource.Remembered.RememberMode.DEFAULT }
        )

    override fun getConfigurationItems(): List<SettingsItem> =
        listOf(
            ToggleSettingsItem(SHOW_PANE_RESIZE_HANDLES),
            ToggleSettingsItem(SHOW_PANE_RESIZE_HANDLES_ON_HOVER),
            ToggleSettingsItem(ANIMATE_PANE_RESIZE),
            ToggleSettingsItem(REMEMBER_INITIAL_PANE_RATIOS),
            DropdownSettingsItem(
                INITIAL_PANE_RATIO_REMEMBER_MODE,
                getItem = {
                    when (it) {
                        InitialPaneRatioSource.Remembered.RememberMode.RATIO ->
                            stringResource(Res.string.pref_interface_initial_pane_ratio_remember_mode_option_ratio)
                        InitialPaneRatioSource.Remembered.RememberMode.SIZE_DP ->
                            stringResource(Res.string.pref_interface_initial_pane_ratio_remember_mode_option_size_dp)
                    }
                }
            )
        )
}
