package dev.toastbits.composekit.settings.ui.item

import androidx.compose.animation.Animatable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.utils.composable.WidthShrinkText

class MultipleChoiceSettingsItem(
    val state: BasicSettingsValueState<Int>,
    val title: String?,
    val subtitle: String?,
    val choice_amount: Int,
    val getChoiceText: (Int) -> String,
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
        val theme = settings_interface.theme
        
        Column {
            Column(Modifier.fillMaxWidth()) {
                ItemTitleText(title, theme, Modifier.padding(bottom = 7.dp))
                ItemText(subtitle, theme)

                Spacer(Modifier.height(10.dp))

                Column(Modifier.padding(start = 15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in 0 until choice_amount) {

                        val colour = remember(i) { Animatable(if (state.get() == i) theme.vibrant_accent else Color.Transparent) }
                        LaunchedEffect(state.get(), theme.vibrant_accent) {
                            colour.animateTo(if (state.get() == i) theme.vibrant_accent else Color.Transparent, TweenSpec(150))
                        }

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
                                .background(colour.value, SETTINGS_ITEM_ROUNDED_SHAPE)
                        ) {
                            Box(Modifier.padding(horizontal = 10.dp)) {
                                WidthShrinkText(
                                    getChoiceText(i),
                                    style = LocalTextStyle.current.copy(color = if (state.get() == i) theme.on_accent else theme.on_background)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
