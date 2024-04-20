package dev.toastbits.composekit.settings.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.utils.composable.ResizableOutlinedTextField

// TODO Styling
class TextFieldSettingsItem(
    val state: PreferencesProperty<String>,
    val title: String?,
    val subtitle: String?,
    val single_line: Boolean = true,
    val getStringError: (String) -> String? = { null },
    val getFieldModifier: @Composable () -> Modifier = { Modifier }
): SettingsItem() {
    override fun resetValues() {
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
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            ItemTitleText(title, settings_interface.theme)
            ItemText(subtitle, settings_interface.theme)

            var input_error: String? by remember { mutableStateOf(null) }
            var current_value: String by remember { mutableStateOf(state.get()) }

            ResizableOutlinedTextField(
                current_value,
                { text ->
                    current_value = text

                    input_error = getStringError(text)
                    if (input_error == null) {
                        state.set(text)
                    }
                },
                getFieldModifier()
                    .fillMaxWidth()
                    .pointerHoverIcon(PointerIcon.Text),
                singleLine = single_line,
                isError = input_error != null,
                label = {
                    input_error?.also {
                        Text(it)
                    }
                }
            )
        }
    }
}
