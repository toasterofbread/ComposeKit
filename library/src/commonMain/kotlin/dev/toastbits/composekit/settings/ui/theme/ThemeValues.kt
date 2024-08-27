package dev.toastbits.composekit.settings.ui

import androidx.compose.ui.graphics.Color
import dev.toastbits.composekit.utils.common.compare
import dev.toastbits.composekit.utils.common.contrastAgainst
import dev.toastbits.composekit.utils.common.getContrasted

interface ThemeValues {
    val background: Color
    val on_background: Color
    val card: Color
    val accent: Color

    enum class Colour {
        BACKGROUND,
        ON_BACKGROUND,
        CARD,
        ACCENT,
        ON_ACCENT,
        VIBRANT_ACCENT;

        fun get(values: ThemeValues): Color =
            when (this) {
                BACKGROUND -> values.background
                ON_BACKGROUND -> values.on_background
                CARD -> values.card
                ACCENT -> values.accent
                ON_ACCENT -> values.on_accent
                VIBRANT_ACCENT -> values.vibrant_accent
            }
    }
}

val ThemeValues.on_accent: Color get() = accent.getContrasted()
val ThemeValues.vibrant_accent: Color get() = makeVibrant(accent)

const val VIBRANT_ACCENT_CONTRAST: Float = 0.2f

fun ThemeValues.makeVibrant(colour: Color, against: Color = background): Color {
    if (colour.compare(background) > 0.8f) {
        return colour.contrastAgainst(against, VIBRANT_ACCENT_CONTRAST, clip = false)
    }
    return colour
}
