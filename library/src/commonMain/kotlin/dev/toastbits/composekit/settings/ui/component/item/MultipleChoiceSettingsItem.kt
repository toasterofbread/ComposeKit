package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues

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

        Column(modifier) {
            Column(Modifier.fillMaxWidth()) {
                ItemTitleText(state.getName(), theme, Modifier.padding(bottom = 7.dp))
                ItemText(state.getDescription(), theme)

                Spacer(Modifier.height(10.dp))

                Column(
                    Modifier.padding(start = 15.dp)
                ) {
                    for (i in 0 until choice_amount) {

                        Row(
                            Modifier
                                .clickable(remember { MutableInteractionSource() }, null) {
                                    state.set(i)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                getChoiceText(i),
                                color = theme.on_background.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            )

                            RadioButton(
                                selected = current_value == i,
                                onClick = { state.set(i) }
                            )
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
