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
        mocha.toNamedTheme(accent of mocha, accent.errorColour of mocha)
    }

private fun Palette.toNamedTheme(accent: PaletteColor, errorAccent: PaletteColor): NamedTheme =
    NamedTheme(
        "Catppuccin ${name.replaceFirstChar { it.uppercaseChar() }} (${accent.definition.label})",
        ThemeValuesData(
            background = Color(base.hex.formatted.toLong(16) or 0x00000000FF000000),
            on_background = Color(text.hex.formatted.toLong(16) or 0x00000000FF000000),
            card = Color(mantle.hex.formatted.toLong(16) or 0x00000000FF000000),
            accent = Color(accent.hex.formatted.toLong(16) or 0x00000000FF000000),
            error = Color(errorAccent.hex.formatted.toLong(16) or 0x00000000FF000000)
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

            return flavour.toNamedTheme(accent, accent.definition.errorColour of flavour)
        }

        break
    }

    return null
}

private val ColorDefinition.errorColour: ColorDefinition
    get() =
        if (this == red) yellow
        else red
