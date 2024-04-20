package dev.toastbits.composekit.settings.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.utils.composable.LargeDropdownMenu
import dev.toastbits.composekit.utils.composable.WidthShrinkText

class DropdownSettingsItem(
    val state: PreferencesProperty<Int>,
    val item_count: Int,
    val getButtonItem: ((Int) -> String)? = null,
    val getItem: @Composable (Int) -> String
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                ItemTitleText(state.name, settings_interface.theme)
                ItemText(state.description, settings_interface.theme)
            }

            var open by remember { mutableStateOf(false) }

            Button(
                { open = !open },
                Modifier.requiredHeight(40.dp),
                shape = SETTINGS_ITEM_ROUNDED_SHAPE,
                colors = ButtonDefaults.buttonColors(
                    containerColor = settings_interface.theme.vibrant_accent,
                    contentColor = settings_interface.theme.on_accent
                )
            ) {
                Text(getButtonItem?.invoke(state.get()) ?: getItem(state.get()))
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    tint = settings_interface.theme.on_accent
                )
            }

            Box(contentAlignment = Alignment.CenterEnd) {
                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(extraSmall = SETTINGS_ITEM_ROUNDED_SHAPE)
                ){
                    LargeDropdownMenu(
                        open,
                        { open = false },
                        item_count,
                        state.get(),
                        {
                            WidthShrinkText(getItem(it))
                        }
                    ) {
                        state.set(it)
                        open = false
                    }
                }
            }
        }
    }
}

inline fun <reified T: Enum<T>> DropdownSettingsItem(
    state: PreferencesProperty<T>,
    noinline getButtonItem: ((T) -> String)? = null,
    noinline getItem: @Composable (T) -> String
): DropdownSettingsItem =
    DropdownSettingsItem(
        state.getConvertedProperty(
            fromProperty = { it.ordinal },
            toProperty = { enumValues<T>()[it] }
        ),
        item_count = enumValues<T>().size,
        getButtonItem = getButtonItem?.let { lambda -> { lambda(enumValues<T>()[it]) } },
        getItem = { getItem(enumValues<T>()[it]) }
    )
