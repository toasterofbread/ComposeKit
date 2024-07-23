package dev.toastbits.composekit.settings.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import kotlinx.serialization.Serializable

@Serializable
data class ThemeValuesData(
    val _background: Int,
    val _on_background: Int,
    val _card: Int,
    val _accent: Int
): ThemeValues {
    override val background: Color get() = Color(_background)
    override val on_background: Color get() = Color(_on_background)
    override val card: Color get() = Color(_card)
    override val accent: Color get() = Color(_accent)

    fun copy(
        background: Color? = null,
        on_background: Color? = null,
        card: Color? = null,
        accent: Color? = null
    ): ThemeValuesData =
        ThemeValuesData(
            background ?: this.background,
            on_background ?: this.on_background,
            card ?: this.card,
            accent ?: this.accent
        )

    companion object {
        fun of(other: ThemeValues): ThemeValuesData {
            if (other is ThemeValuesData) {
                return other
            }

            return ThemeValuesData(
                other.background,
                other.on_background,
                other.card,
                other.accent
            )
        }

        fun fromColourScheme(colour_scheme: ColorScheme): ThemeValuesData =
            ThemeValuesData(
                background = colour_scheme.background,
                on_background = colour_scheme.onBackground,
                card = colour_scheme.surfaceColorAtElevation(2.dp),
                accent = colour_scheme.primary
            )
    }
}

fun ThemeValuesData(
    background: Color,
    on_background: Color,
    card: Color,
    accent: Color
): ThemeValuesData =
    ThemeValuesData(
        background.toArgb(),
        on_background.toArgb(),
        card.toArgb(),
        accent.toArgb()
    )
