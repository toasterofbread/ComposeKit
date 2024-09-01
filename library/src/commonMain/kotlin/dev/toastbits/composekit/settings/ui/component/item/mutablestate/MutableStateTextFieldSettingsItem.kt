package dev.toastbits.composekit.settings.ui.component.item.mutablestate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import dev.toastbits.composekit.platform.MutableStatePreferencesProperty
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.component.item.TextFieldSettingsItem

fun MutableStateTextFieldSettingsItem(
    value: String,
    onSet: (String) -> Unit,
    getPropertyName: @Composable () -> String,
    getPropertyDescription: @Composable () -> String?
): TextFieldSettingsItem {
    val nameState: MutableState<String> =
        object : MutableState<String> {
            override var value: String
                get() = value
                set(value) { onSet(value) }

            override fun component1(): String = value

            override fun component2(): (String) -> Unit = onSet
        }

    val property: PreferencesProperty<String> = MutableStatePreferencesProperty(nameState, getPropertyName, getPropertyDescription)
    return TextFieldSettingsItem(property)
}
