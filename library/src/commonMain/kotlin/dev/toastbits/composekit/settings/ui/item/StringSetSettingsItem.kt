package dev.toastbits.composekit.settings.ui.item

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.composable.ScrollabilityIndicatorColumn
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.settings.ui.theme.on_accent
import dev.toastbits.composekit.utils.composable.ShapedIconButton
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class StringSetSettingsItem(
    val state: PreferencesProperty<Set<String>>,
    val add_dialog_title: StringResource,
    val msg_item_already_added: StringResource,
    val msg_set_empty: StringResource,
    val single_line_content: Boolean = true,
    val max_height: Dp = 300.dp,
    val itemToText: @Composable (String) -> String = { it },
    val textToItem: (String) -> String = { it },
    val getFieldModifier: @Composable () -> Modifier = { Modifier }
): SettingsItem() {
    override suspend fun resetValues() {
        state.reset()
    }

    override fun getProperties(): List<PreferencesProperty<*>> = listOf(state)

    @Composable
    override fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    ) {
        val value: Set<String> by state.observe()
        val theme = settings_interface.theme
        val icon_button_colours = IconButtonDefaults.iconButtonColors(
            containerColor = theme.accent,
            contentColor = theme.on_accent,
            disabledContainerColor = theme.accent.copy(alpha = 0.5f)
        )

        var show_add_item_dialog: Boolean by remember { mutableStateOf(false) }
        if (show_add_item_dialog) {
            var new_item_content: String by remember { mutableStateOf("") }
            val item_already_added = value.contains(new_item_content)
            val can_add_item = new_item_content.isNotEmpty() && !item_already_added

            AlertDialog(
                onDismissRequest = { show_add_item_dialog = false },
                confirmButton = {
                    Crossfade(can_add_item) { enabled ->
                        ShapedIconButton(
                            {
                                state.set(value.plus(textToItem(new_item_content)))
                                show_add_item_dialog = false
                            },
                            colours = icon_button_colours,
                            enabled = enabled
                        ) {
                            Icon(Icons.Default.Done, null)
                        }
                    }
                },
                dismissButton = {
                    ShapedIconButton(
                        {
                            show_add_item_dialog = false
                        },
                        colours = icon_button_colours
                    ) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                title = { Text(stringResource(add_dialog_title)) },
                text = {
                    TextField(
                        new_item_content,
                        { new_item_content = it },
                        getFieldModifier(),
                        singleLine = single_line_content,
                        isError = item_already_added,
                        label =
                        if (item_already_added) {{ Text(stringResource(msg_item_already_added)) }}
                        else null
                    )
                }
            )
        }

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ItemTitleText(state.getName(), theme)
                    settings_interface.ItemText(state.getDescription(), theme)
                }

                ShapedIconButton(
                    { show_add_item_dialog = true },
                    colours = icon_button_colours
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }

            Crossfade(value, Modifier.fillMaxWidth()) { set ->
                if (set.isEmpty()) {
                    Text(
                        stringResource(msg_set_empty),
                        Modifier.fillMaxWidth().padding(top = 20.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else {
                    val scroll_state = rememberLazyListState()
                    ScrollabilityIndicatorColumn(scroll_state, Modifier.heightIn(max = max_height)) {
                        LazyColumn(state = scroll_state) {
                            for (item in set) {
                                item {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(itemToText(item), Modifier.fillMaxWidth().weight(1f))

                                        IconButton(
                                            { state.set(set.minus(item)) }
                                        ) {
                                            Icon(Icons.Default.Remove, null, tint = theme.on_background)
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
}
