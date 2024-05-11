@file:Suppress("MemberVisibilityCanBePrivate")

package dev.toastbits.composekit.settings.ui.item

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PlatformPreferencesListener
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.settings.ui.Theme
import dev.toastbits.composekit.utils.composable.LinkifyText
import dev.toastbits.composekit.utils.composable.WidthShrinkText
import dev.toastbits.composekit.platform.PlatformContext

val SETTINGS_ITEM_ROUNDED_SHAPE = RoundedCornerShape(20.dp)

abstract class SettingsItem {
    abstract fun resetValues()

    abstract fun getProperties(): List<PreferencesProperty<*>>

    @Composable
    abstract fun Item(
        settings_interface: SettingsInterface,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        modifier: Modifier
    )

    companion object {
        @Composable
        fun ItemTitleText(text: String?, theme: Theme, modifier: Modifier = Modifier, max_lines: Int = 1) {
            if (text?.isNotBlank() == true) {
                WidthShrinkText(
                    text,
                    modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium.copy(color = theme.on_background),
                    max_lines = max_lines
                )
            }
        }

        @Composable
        fun ItemText(
            context: PlatformContext,
            text: String?,
            theme: Theme,
            colour: Color = theme.on_background.copy(alpha = 0.75f),
            linkify: Boolean = true
        ) {
            if (text?.isNotBlank() == true) {
                val style: TextStyle = MaterialTheme.typography.bodySmall.copy(color = colour)
                if (linkify) LinkifyText(context, text, theme.accent, style = style)
                else Text(text, style = style)
            }
        }
    }
}
