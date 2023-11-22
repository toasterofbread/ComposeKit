package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.toasterofbread.composekit.platform.PlatformPreferences
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage
import com.toasterofbread.composekit.utils.common.toFloat
import com.toasterofbread.composekit.utils.composable.SubtleLoadingIndicator
import com.toasterofbread.composekit.utils.composable.WidthShrinkText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class FileSettingsItem(
    val state: BasicSettingsValueState<String>,
    val title: String,
    val subtitle: String?,
    val getPathLabel: @Composable (String) -> String,
    val onSelectRequested: (
        setValue: (String) -> Unit,
        showDialog: (Dialog) -> Unit,
    ) -> Unit,
): SettingsItem() {
    data class Dialog(
        val title: String,
        val body: String,
        val accept_button: String,
        val deny_button: String,
        val onSelected: suspend (accepted: Boolean) -> Unit
    )

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

    private var current_dialog: Dialog? by mutableStateOf(null)
    private val coroutine_scope = CoroutineScope(Job())

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        var action_in_progress: Boolean by remember { mutableStateOf(false) }
        LaunchedEffect(current_dialog) {
            action_in_progress = false
            coroutine_scope.coroutineContext.job.cancelChildren()
        }

        current_dialog?.also { dialog ->
            AlertDialog(
                { current_dialog = null },
                title = {
                    WidthShrinkText(dialog.title)
                },
                text = {
                    Text(dialog.body)
                },
                confirmButton = {
                    Button(
                        {
                            coroutine_scope.launch(Dispatchers.Default) {
                                action_in_progress = true
                                dialog.onSelected(true)
                                action_in_progress = false

                                if (current_dialog == dialog) {
                                    current_dialog = null
                                }
                            }
                        },
                        enabled = !action_in_progress
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val alpha: Float by animateFloatAsState(action_in_progress.toFloat())
                            SubtleLoadingIndicator(Modifier.alpha(alpha))
                            Text(
                                dialog.accept_button,
                                Modifier.alpha(1f - alpha)
                            )
                        }
                    }
                },
                dismissButton = {
                    Crossfade(!action_in_progress) { enabled ->
                        Button(
                            {
                                if (!enabled) {
                                    return@Button
                                }

                                coroutine_scope.launch(Dispatchers.Default) {
                                    action_in_progress = true
                                    dialog.onSelected(false)
                                    action_in_progress = false

                                    if (current_dialog == dialog) {
                                        current_dialog = null
                                    }
                                }
                            },
                            enabled = enabled
                        ) {
                            Text(dialog.deny_button)
                        }
                    }
                }
            )
        }

        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ItemTitleText(title, settings_interface.theme)
                    ItemText(subtitle, settings_interface.theme)
                }

                IconButton({
                    onSelectRequested(
                        { path ->
                            state.set(path)
                        },
                        { dialog ->
                            current_dialog = dialog
                        }
                    )
                }) {
                    Icon(Icons.Default.Folder, null)
                }

                IconButton({
                    state.reset()
                }) {
                    Icon(Icons.Default.Refresh, null)
                }
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .background(settings_interface.theme.accent, RoundedCornerShape(16.dp))
                    .padding(10.dp)
            ) {
                Text(
                    getPathLabel(state.get()),
                    style = MaterialTheme.typography.bodySmall,
                    color = settings_interface.theme.on_accent
                )
            }
        }
    }
}