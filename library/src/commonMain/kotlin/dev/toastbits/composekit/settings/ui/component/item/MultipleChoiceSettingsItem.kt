package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.settings.ui.vibrant_accent
import dev.toastbits.composekit.settings.ui.on_accent
import dev.toastbits.composekit.utils.composable.WidthShrinkText

class MultipleChoiceSettingsItem(
    val state: PreferencesProperty<Int>,
    val choice_amount: Int,
    val getChoiceText: @Composable (Int) -> String
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
        val current_value: Int by state.observe()

        Column {
            Column(Modifier.fillMaxWidth()) {
                ItemTitleText(state.getName(), theme, Modifier.padding(bottom = 7.dp))
                ItemText(state.getDescription(), theme)

                Spacer(Modifier.height(10.dp))

                Column(Modifier.padding(start = 15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in 0 until choice_amount) {
                        val colour: Color by animateColorAsState(if (current_value == i) theme.vibrant_accent else Color.Transparent)

                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .border(
                                    Dp.Hairline,
                                    theme.on_background,
                                    SETTINGS_ITEM_ROUNDED_SHAPE
                                )
                                .fillMaxWidth()
                                .height(40.dp)
                                .clickable(remember { MutableInteractionSource() }, null) {
                                    state.set(i)
                                }
                                .background(colour, SETTINGS_ITEM_ROUNDED_SHAPE)
                        ) {
                            Box(Modifier.padding(horizontal = 10.dp)) {
                                WidthShrinkText(
                                    getChoiceText(i),
                                    style = LocalTextStyle.current.copy(color = if (current_value == i) theme.on_accent else theme.on_background),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

inline fun <reified T: Enum<T>> MultipleChoiceSettingsItem(
    state: PreferencesProperty<T>,
    noinline getChoiceText: @Composable (T) -> String,
): MultipleChoiceSettingsItem =
    MultipleChoiceSettingsItem(
        state.getConvertedProperty(
            fromProperty = { it.ordinal },
            toProperty = { enumValues<T>()[it] }
        ),
        choice_amount = enumValues<T>().size,
        getChoiceText = {
            getChoiceText(enumValues<T>()[it])
        }
    )
