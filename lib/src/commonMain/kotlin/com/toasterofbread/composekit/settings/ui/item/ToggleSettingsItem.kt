package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toasterofbread.composekit.platform.PlatformPreferences
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage
import com.toasterofbread.composekit.settings.ui.Theme

class ToggleSettingsItem(
    val state: BasicSettingsValueState<Boolean>,
    val title: String?,
    val subtitle: String?,
    val title_max_lines: Int = 2,
    val getEnabled: @Composable () -> Boolean = { true },
    val getValueOverride: @Composable () -> Boolean? = { null },
    val getSubtitleOverride: @Composable () -> String? = { null },
    val checker: ((target: Boolean, setLoading: (Boolean) -> Unit, (allow_change: Boolean) -> Unit) -> Unit)? = null
): SettingsItem() {
    private var loading: Boolean by mutableStateOf(false)

    override fun initialiseValueStates(prefs: PlatformPreferences, default_provider: (String) -> Any) {
        state.init(prefs, default_provider)
    }

    override fun releaseValueStates(prefs: PlatformPreferences) {
        state.release(prefs)
    }

    override fun setEnableAutosave(value: Boolean) {
        state.setEnableAutosave(value)
    }

    override fun PlatformPreferences.Editor.saveItem() {
        with (state) {
            save()
        }
    }

    override fun resetValues() {
        state.reset()
    }

    override fun getKeys(): List<String> = state.getKeys()

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        val theme: Theme = settings_interface.theme
        val enabled: Boolean = getEnabled()
        val value_override: Boolean? = getValueOverride()

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                ItemTitleText(title, theme, max_lines = title_max_lines)
                ItemText(getSubtitleOverride() ?: subtitle, theme)
            }

            Crossfade(loading) {
                if (it) {
                    CircularProgressIndicator(color = theme.on_background)
                }
                else {
                    Switch(
                        value_override ?: state.get(),
                        onCheckedChange = null,
                        enabled = enabled,
                        modifier =
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = enabled
                            ) {
                                if (checker == null) {
                                    state.set(!state.get())
                                    return@clickable
                                }

                                checker.invoke(
                                    !state.get(),
                                    { l ->
                                        loading = l
                                    }
                                ) { allow_change ->
                                    if (allow_change) {
                                        state.set(!state.get())
                                    }
                                    loading = false
                                }
                            },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = theme.vibrant_accent,
                            checkedTrackColor = theme.vibrant_accent.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}
