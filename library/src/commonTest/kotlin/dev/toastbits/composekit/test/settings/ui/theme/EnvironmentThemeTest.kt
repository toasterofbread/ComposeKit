package dev.toastbits.composekit.test.settings.ui.theme

import androidx.compose.ui.graphics.Color
import dev.toastbits.composekit.settings.ui.theme.NamedTheme
import dev.toastbits.composekit.settings.ui.theme.getCurrentEnvironmentTheme
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EnvironmentThemeTest {
    @Test
    fun testEmptyEnvironmentTheme() {
        val theme: NamedTheme? = getCurrentEnvironmentTheme { null }
        assertNull(theme)
    }

    @Test
    fun testCatppuccinEnvironmentTheme() {
        for ((theme_name, accent_colour) in mapOf(
            "Catppuccin-Mocha-Standard-Mauve" to Color(203, 166, 247),
            "Catppuccin-Mocha-Standard-Green" to Color(166, 227, 161),
            "Catppuccin-Latte-Standard-Mauve" to Color(136, 57, 239),
            "Catppuccin-Latte-Standard-Green" to Color(64, 160, 43)
        )) {
            val theme: NamedTheme? =
                getCurrentEnvironmentTheme { name ->
                    if (name == "GTK_THEME") theme_name
                    else null
                }

            assertNotNull(theme, theme_name)
            assertContains(theme.name.lowercase(), "catppuccin", message = theme_name)

            assertEquals(theme.theme.accent, accent_colour, theme_name)
        }
    }
}
