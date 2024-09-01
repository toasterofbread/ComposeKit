package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import dev.toastbits.composekit.settings.ui.on_accent
import dev.toastbits.composekit.utils.composable.LargeDropdownMenu
import dev.toastbits.composekit.utils.composable.WidthShrinkText

class DropdownSettingsItem(
    val state: PreferencesProperty<Int>,
    val item_count: Int,
    val getButtonItem: (@Composable (Int) -> String)? = null,
    val getItem: @Composable (Int) -> String
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
        var current_value: Int by state.observe()

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                ItemTitleText(state.getName(), theme)
                ItemText(state.getDescription(), theme)
            }

            var open by remember { mutableStateOf(false) }

            Button(
                { open = !open },
                Modifier
                    .requiredHeight(40.dp)
                    .align(Alignment.CenterVertically),
                shape = SETTINGS_ITEM_ROUNDED_SHAPE,
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.vibrant_accent,
                    contentColor = theme.on_accent
                )
            ) {
                Text(getButtonItem?.invoke(current_value) ?: getItem(current_value))
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    tint = theme.on_accent
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
                        current_value,
                        {
                            Text(getItem(it), Modifier.fillMaxWidth())
                        }
                    ) {
                        current_value = it
                        open = false
                    }
                }
            }
        }
    }
}

inline fun <reified T: Enum<T>> DropdownSettingsItem(
    state: PreferencesProperty<T>,
    noinline getButtonItem: (@Composable (T) -> String)? = null,
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
