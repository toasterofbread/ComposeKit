package dev.toastbits.composekit.settings.ui.component.item

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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.settings.ui.vibrant_accent

class ToggleSettingsItem(
    val state: PreferencesProperty<Boolean>,
    val getEnabled: @Composable () -> Boolean = { true },
    val getValueOverride: @Composable () -> Boolean? = { null },
    val getSubtitleOverride: @Composable () -> String? = { null },
    val checker: ((target: Boolean, setLoading: (Boolean) -> Unit, (allow_change: Boolean) -> Unit) -> Unit)? = null
): SettingsItem() {
    private var loading: Boolean by mutableStateOf(false)

    override suspend fun resetValues() {
        state.reset()
    }

    override fun getProperties(): List<PreferencesProperty<*>> = listOf(state)

    @Composable
    override fun Item(
        modifier: Modifier
    ) {
        val theme: ThemeValues = LocalApplicationTheme.current
        val enabled: Boolean = getEnabled()
        val value_override: Boolean? = getValueOverride()
        var current_value: Boolean by state.observe()

        Row(
            modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                ItemTitleText(state.getName(), theme)
                ItemText(getSubtitleOverride() ?: state.getDescription(), theme)
            }

            Crossfade(loading) {
                if (it) {
                    CircularProgressIndicator(color = theme.on_background)
                }
                else {
                    Switch(
                        value_override ?: current_value,
                        onCheckedChange = null,
                        enabled = enabled,
                        modifier =
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = enabled
                            ) {
                                if (checker == null) {
                                    current_value = !current_value
                                    return@clickable
                                }

                                checker.invoke(
                                    !current_value,
                                    { l ->
                                        loading = l
                                    }
                                ) { allow_change ->
                                    if (allow_change) {
                                        current_value = !current_value
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
