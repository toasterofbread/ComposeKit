package dev.toastbits.composekit.settings.ui.item

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.settings.ui.Theme
import dev.toastbits.composekit.utils.composable.WidthShrinkText

class MultipleChoiceSettingsItem(
    val state: PreferencesProperty<Int>,
    val choice_amount: Int,
    val getChoiceText: (Int) -> String
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
        val theme: Theme = settings_interface.theme
        val current_value: Int by state.observe()

        Column {
            Column(Modifier.fillMaxWidth()) {
                ItemTitleText(state.name, theme, Modifier.padding(bottom = 7.dp))
                ItemText(state.description, theme)

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
                                    style = LocalTextStyle.current.copy(color = if (state.get() == i) theme.on_accent else theme.on_background),
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
    noinline getChoiceText: (T) -> String,
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
