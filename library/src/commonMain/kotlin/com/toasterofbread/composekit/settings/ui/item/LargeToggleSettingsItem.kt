package dev.toastbits.composekit.settings.ui.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.settings.ui.Theme
import dev.toastbits.composekit.utils.composable.SubtleLoadingIndicator
import dev.toastbits.composekit.utils.composable.WidthShrinkText
import dev.toastbits.composekit.utils.modifier.background

class LargeToggleSettingsItem(
    val state: PreferencesProperty<Boolean>,
    val enabled_text: String? = null,
    val disabled_text: String? = null,
    val enable_button: String,
    val disable_button: String,
    val enabledContent: (@Composable (Modifier) -> Unit)? = null,
    val disabledContent: (@Composable (Modifier) -> Unit)? = null,
    val prerequisite_value: PreferencesProperty<Boolean>? = null,
    val warningDialog: (@Composable (dismiss: () -> Unit, openPage: (Int, Any?) -> Unit) -> Unit)? = null,
    val infoButton: (@Composable (enabled: Boolean, showing_extra_state: MutableState<Boolean>) -> Unit)? = null,
    val extra_items: List<SettingsItem> = emptyList(),
    val show_button: Boolean = true,
    val onClicked: (target: Boolean, setEnabled: (Boolean) -> Unit, setLoading: (Boolean) -> Unit, openPage: (Int, Any?) -> Unit) -> Unit =
        { target, setEnabled, _, _ -> setEnabled(target) }
): SettingsItem() {
    override fun resetValues() {
        state.reset()
        for (item in extra_items) {
            item.resetValues()
        }
    }

    override fun getProperties(): List<PreferencesProperty<*>> = listOf(state) + extra_items.flatMap { it.getProperties() }

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        val theme: Theme = settings_interface.theme
        val shape: RoundedCornerShape = RoundedCornerShape(25.dp)
        var loading: Boolean by remember { mutableStateOf(false) }

        val showing_extra_state: MutableState<Boolean> = remember { mutableStateOf(false) }
        var showing_dialog: (@Composable (dismiss: () -> Unit, openPage: (Int, Any?) -> Unit) -> Unit)? by remember { mutableStateOf(null) }

        showing_dialog?.invoke(
            { showing_dialog = null },
            openPage
        )

        val state_value by state.observe()

        LaunchedEffect(state_value) {
            if (!state_value) {
                showing_extra_state.value = false
            }
        }

        AnimatedVisibility(
            prerequisite_value?.get() != false,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Crossfade(state_value) { enabled ->
                CompositionLocalProvider(LocalContentColor provides if (!enabled) theme.on_background else theme.on_accent) {
                    Column(
                        modifier
                            .background(
                                if (!enabled) theme.background else theme.vibrant_accent,
                                shape
                            )
                            .border(2.dp, theme.vibrant_accent, shape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .fillMaxSize()
                            .animateContentSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Row(
                            Modifier.height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (if (enabled) enabledContent else disabledContent)?.invoke(Modifier.weight(1f).padding(vertical = 5.dp))
                            (if (enabled) enabled_text else disabled_text)?.also { WidthShrinkText(it, Modifier.fillMaxWidth().weight(1f)) }

                            AnimatedVisibility(show_button) {
                                Button(
                                    {
                                        if (!enabled && warningDialog != null) {
                                            showing_dialog = warningDialog
                                        }
                                        else {
                                            onClicked(
                                                !enabled,
                                                { state.set(it) },
                                                { loading = it },
                                                openPage
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (enabled) theme.background else theme.vibrant_accent,
                                        contentColor = if (enabled) theme.on_background else theme.on_accent
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        this@Row.AnimatedVisibility(loading, enter = fadeIn(), exit = fadeOut()) {
                                            SubtleLoadingIndicator()
                                        }

                                        val text_alpha = animateFloatAsState(if (loading) 0f else 1f)
                                        Text(
                                            if (enabled) disable_button else enable_button,
                                            Modifier.graphicsLayer { alpha = text_alpha.value }
                                        )
                                    }
                                }
                            }

                            if (infoButton != null) {
                                infoButton.invoke(enabled, showing_extra_state)
                            }
                        }

                        if (showing_extra_state.value) {
                            Column(
                                Modifier
                                    .padding(vertical = 5.dp)
                                    .background(shape) { theme.background }
                                    .padding(horizontal = 10.dp, vertical = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CompositionLocalProvider(LocalContentColor provides theme.on_background) {
                                    for (item in extra_items) {
                                        item.Item(settings_interface, openPage, openCustomPage, Modifier)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
