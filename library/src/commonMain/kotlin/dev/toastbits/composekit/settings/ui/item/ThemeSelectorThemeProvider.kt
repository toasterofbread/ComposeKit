package dev.toastbits.composekit.settings.ui.item

import dev.toastbits.composekit.settings.ui.theme.ThemeValues
import dev.toastbits.composekit.settings.ui.theme.NamedTheme

interface ThemeSelectorThemeProvider {
    fun getThemeCount(): Int
    fun getTheme(index: Int): NamedTheme?
    fun isThemeEditable(index: Int): Boolean
    fun onThemeEdited(index: Int, theme: ThemeValues, theme_name: String)
    suspend fun createTheme(index: Int)
    suspend fun removeTheme(index: Int)
}
