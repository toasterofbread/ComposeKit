package dev.toastbits.composekit.settings.ui

import com.catppuccin.kotlin.*
import com.catppuccin.kotlin.color.*
import androidx.compose.ui.graphics.Color

fun getDefaultCatppuccinThemes(): List<NamedTheme> =
    listOf(
        mauve,
        lavender,
        red,
        yellow,
        green,
        teal,
        pink,
        sapphire,
        rosewater,
        peach,
        sky,
        maroon,
        blue,
        flamingo
    ).map { accent ->
        mocha.toNamedTheme(accent of mocha)
    }

private fun Palette.toNamedTheme(accent: PaletteColor): NamedTheme =
    NamedTheme(
        "Catppuccin ${name.replaceFirstChar { it.uppercaseChar() }} (${accent.definition.label})",
        ThemeValuesData(
            background = Color(base.hex.intValue),
            on_background = Color(text.hex.intValue),
            card = Color(mantle.hex.intValue),
            accent = Color(accent.hex.intValue)
        )
    )

internal fun getCatppuccinTheme(target_flavour: String, target_accent: String): NamedTheme? {
    for (flavour in allPalettes) {
        if (flavour.name.lowercase() != target_flavour.lowercase()) {
            continue
        }

        for (accent in flavour.colors) {
            if (accent.definition.label.lowercase() != target_accent.lowercase()) {
                continue
            }

            return flavour.toNamedTheme(accent)
        }

        break
    }

    return null
}
