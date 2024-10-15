@file:Suppress("MemberVisibilityCanBePrivate")

package dev.toastbits.composekit.settings.ui.component.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import dev.toastbits.composekit.platform.Platform
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.utils.composable.LinkifyText

val SETTINGS_ITEM_ROUNDED_SHAPE: CornerBasedShape
    @Composable
    get() = when (Platform.current) {
        Platform.ANDROID -> MaterialTheme.shapes.extraLarge
        Platform.DESKTOP,
        Platform.WEB -> MaterialTheme.shapes.small
    }

abstract class SettingsItem {
    abstract suspend fun resetValues()

    abstract fun getProperties(): List<PreferencesProperty<*>>

    @Composable
    abstract fun Item(
        modifier: Modifier
    )

    @Composable
    open fun showItem(): Boolean = true

    companion object {
        @Composable
        fun ItemTitleText(text: String?, theme: ThemeValues, modifier: Modifier = Modifier) {
            if (text?.isNotBlank() == true) {
                Text(
                    text,
                    modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium.copy(color = theme.on_background)
                )
            }
        }

        @Composable
        fun ItemText(
            text: String?,
            theme: ThemeValues,
            colour: Color = theme.on_background.copy(alpha = 0.75f),
            linkify: Boolean = true
        ) {
            if (text?.isNotBlank() == true) {
                val style: TextStyle = MaterialTheme.typography.bodySmall.copy(color = colour)
                if (linkify) LinkifyText(text, theme.accent, style = style)
                else Text(text, style = style)
            }
        }
    }
}
