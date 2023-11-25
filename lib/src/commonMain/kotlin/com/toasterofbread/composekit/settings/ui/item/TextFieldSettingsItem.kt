package com.toasterofbread.composekit.settings.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toasterofbread.composekit.platform.PlatformPreferences
import com.toasterofbread.composekit.settings.ui.SettingsInterface
import com.toasterofbread.composekit.settings.ui.SettingsPage
import com.toasterofbread.composekit.utils.composable.ResizableOutlinedTextField

// TODO Styling
class TextFieldSettingsItem(
    val state: BasicSettingsValueState<String>,
    val title: String?,
    val subtitle: String?,
    val single_line: Boolean = true,
    val getStringError: (String) -> String? = { null }
): SettingsItem() {
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
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            ItemTitleText(title, settings_interface.theme)
            ItemText(subtitle, settings_interface.theme)

            var input_error: String? by remember { mutableStateOf(null) }
            var current_value: String by remember { mutableStateOf(state.get()) }

            state.onChanged(Unit) {
                current_value = it
                input_error = getStringError(it)
            }

            ResizableOutlinedTextField(
                current_value,
                { text ->
                    current_value = text
                    input_error = getStringError(text)

                    if (input_error == null) {
                        state.set(text)
                    }
                },
                Modifier.fillMaxWidth(),
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
