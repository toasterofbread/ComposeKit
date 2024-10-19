package dev.toastbits.composekit.settings.ui.component.item

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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.composable.ResizableOutlinedTextField

// TODO Styling
class TextFieldSettingsItem(
    val state: PreferencesProperty<String>,
    val single_line: Boolean = true,
    val getStringErrorProvider: @Composable () -> TextFieldErrorMessageProvider? = { null },
    val getFieldModifier: @Composable () -> Modifier = { Modifier }
): SettingsItem() {
    override suspend fun resetValues() {
        state.reset()
    }

    override fun getProperties(): List<PreferencesProperty<*>> = listOf(state)

    @Composable
    override fun Item(
        modifier: Modifier
    ) {
        val theme: ThemeValues = LocalApplicationTheme.current
        val string_error_provider: TextFieldErrorMessageProvider? = getStringErrorProvider()

        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            ItemTitleText(state.getName(), theme)
            ItemText(state.getDescription(), theme)

            var input_error: String? by remember { mutableStateOf(null) }
            var current_value: String by remember { mutableStateOf("") }

            LaunchedEffect(state) {
                current_value = state.get()
            }

            ResizableOutlinedTextField(
                current_value,
                { text ->
                    current_value = text

                    input_error = string_error_provider?.invoke(text)
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

typealias TextFieldErrorMessageProvider = (String) -> String?
